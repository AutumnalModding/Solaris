package xyz.lilyflower.solaris.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Constructor;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.Logger;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.logging.log4j.LogManager;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.lilyflower.solaris.api.TransformerSettingsModule;
import xyz.lilyflower.solaris.core.settings.SolarisTransformerSettings;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.util.reflect.ClasspathScanning;
import xyz.lilyflower.solaris.util.SolarisExtensions;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("SolarisBootstrap")
public class SolarisBootstrap implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Solaris Bootstrap");
    public static final Logger DEBUG_LOG = LogManager.getLogger("Solaris Debug");
    private static final Path MODS_DIRECTORY_PATH = new File(Launch.minecraftHome, "mods/").toPath();
    public static final boolean DEBUG_ENABLED = Files.exists(Paths.get(".classes/")) || System.getProperty("solaris.debug") != null;

    static {
        LOGGER.info("Spinning up...");
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        SolarisTransformer transformer = new SolarisTransformer();
        try {
            ByteBuddyAgent.AttachmentProvider.Compound provider = new ByteBuddyAgent.AttachmentProvider.Compound(
                    ByteBuddyAgent.AttachmentProvider.ForModularizedVm.INSTANCE,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JVM_ROOT,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JDK_ROOT,
                    ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.MACINTOSH,
                    ByteBuddyAgent.AttachmentProvider.ForUserDefinedToolsJar.INSTANCE
            ); // <-- DEFAULT, minus JNA.
            Instrumentation agent = ByteBuddyAgent.install(provider);
            agent.addTransformer(transformer, true);
        } catch (ExceptionInInitializerError | IllegalStateException error) {
            LOGGER.error("Failed to initialize agent with the standard method. Trying JNA. This might crash on Windows!");
            try {
                Class.forName("com.sun.jna.Native");
                Instrumentation agent = ByteBuddyAgent.install(ByteBuddyAgent.AttachmentProvider.ForEmulatedAttachment.INSTANCE);
                agent.addTransformer(transformer, true);
            } catch (ExceptionInInitializerError | IllegalStateException | UnsatisfiedLinkError | ClassNotFoundException failure) {
                LoggingHelper.oopsie(LOGGER, "Failed to initialize agent, running in mixin-only mode - try running with a Java 8 JDK.", failure);
            }
        }

        String name = ManagementFactory.getRuntimeMXBean().getName();
        long pid = Long.parseLong(name.split("@")[0]);
        LOGGER.info("Process ID: {}", pid);

        String enabled = System.getProperty("solaris.disableTransformerSettings");
        if (enabled == null) {
            LOGGER.info("If it freezes here, try restarting with -Dsolaris.disableTransformerSettings as a JVM argument!");
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
            SolarisTransformerSettings.load();
        }

        LaunchClassLoader loader = Launch.classLoader;
        loader.addTransformerExclusion("com.unascribed.ears");
        LOGGER.info("Attach completed. Handing over control...");
    }

    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getModContainerClass() { return null; }
    @Override public String getAccessTransformerClass() { return null; }
    @Override public String[] getASMTransformerClass() { return new String[0]; }
}
