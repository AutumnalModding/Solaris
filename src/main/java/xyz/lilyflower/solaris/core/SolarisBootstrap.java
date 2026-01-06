package xyz.lilyflower.solaris.core;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.net.URISyntaxException;
import java.lang.reflect.Constructor;
import org.apache.logging.log4j.Logger;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.logging.log4j.LogManager;
import java.lang.instrument.Instrumentation;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.ITweaker;
import cpw.mods.fml.relauncher.CoreModManager;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.lilyflower.solaris.api.TransformerSettingsModule;
import xyz.lilyflower.solaris.core.settings.SolarisTransformerSettings;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.util.ClasspathScanning;

@SuppressWarnings("unused") // How early can we go?
public class SolarisBootstrap implements ITweaker {
    public static final Logger LOGGER = LogManager.getLogger("Solaris Bootstrap");

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
        if (location == null) return;
        if (!"file".equals(location.getProtocol())) return;
        try {
            MixinBootstrap.getPlatform().addContainer(location.toURI());
            String mod = new File(location.toURI()).getName();
            CoreModManager.getReparseableCoremods().add(mod);
        } catch (URISyntaxException exception) {
            LoggingHelper.oopsie(LOGGER, "FAILED TO LOAD COREMOD", exception);
            FMLCommonHandler.instance().exitJava(400, true);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launch) {
        // You either die a FakeTransformerExclusions...
        // ...or live long enough to see yourself an LCL call.
        launch.addTransformerExclusion("com.unascribed.ears");
        SolarisTransformer.LOADER = launch;
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    static {
        LOGGER.info("Spinning up...");
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        try {
            ByteBuddyAgent.AttachmentProvider.Compound provider = new ByteBuddyAgent.AttachmentProvider.Compound(
                    ByteBuddyAgent.AttachmentProvider.ForModularizedVm.INSTANCE,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JVM_ROOT,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JDK_ROOT,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.MACINTOSH,
                    ByteBuddyAgent.AttachmentProvider.ForUserDefinedToolsJar.INSTANCE
            ); // <-- DEFAULT, minus JNA.
            Instrumentation agent = ByteBuddyAgent.install(provider);
            agent.addTransformer(new SolarisTransformer(), true);
        } catch (ExceptionInInitializerError error) {

            LOGGER.error("Failed to initialize agent with the standard method. Trying JNA. This might crash on Windows!");
            try {
                Class.forName("com.sun.jna.Native"); // Last resort - will sometimes fail on Windows.
                Instrumentation agent = ByteBuddyAgent.install(ByteBuddyAgent.AttachmentProvider.ForEmulatedAttachment.INSTANCE);
                agent.addTransformer(new SolarisTransformer(), true);
            } catch (ExceptionInInitializerError | UnsatisfiedLinkError | ClassNotFoundException failure) {
                LoggingHelper.oopsie(LOGGER, "Failed to initialize agent, running in mixin-only mode - try running with a Java 8 JDK.", failure);
            }
        }

        String name = ManagementFactory.getRuntimeMXBean().getName();
        long pid = Long.parseLong(name.split("@")[0]);
        LOGGER.info("Process ID: {}", pid);

        String enabled = System.getProperty("solaris.earlyconfig");
        LOGGER.info("If it freezes here, try restarting with -Dsolaris.earlyconfig=false as a JVM argument!");
        if (enabled == null || !enabled.equals("false")) {
            LOGGER.info("Scanning transformer settings modules..."); int count = 0; // love that you can just Do This for compacting lines.
            List<Class<TransformerSettingsModule>> modules = ClasspathScanning.implementations(TransformerSettingsModule.class, false, false);
            for (Class<TransformerSettingsModule> module : modules) {
                try {
                    LOGGER.info("Attempting to register module {}...", module.getSimpleName());
                    ++count;
                    Constructor<? extends TransformerSettingsModule> constructor = module.getConstructor();
                    TransformerSettingsModule instance = constructor.newInstance();
                    instance.init();
                } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                         IllegalAccessException exception) {
                    LoggingHelper.oopsie(LOGGER, "FAILED TO LOAD TRANSFORMER SETTINGS: " + module.getSimpleName(), exception);
                }
            }
            LOGGER.info("Scan complete. Loaded {} modules.", count);

            File dir = new File("config/solaris/");
            if (dir.exists() || dir.mkdirs()) {
                SolarisTransformerSettings.load(new File("config/solaris/early.cfg"));
            }
        }

        LOGGER.info("Attach completed. Handing over control...");
    }
}
