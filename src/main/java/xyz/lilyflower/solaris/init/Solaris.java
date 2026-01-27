package xyz.lilyflower.solaris.init;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lotr.common.LOTRMod;
import lotr.common.LOTRTime;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.configuration.modules.SolarisLOTR;
import xyz.lilyflower.solaris.command.LTRDebuggerCommand;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.event.SolarisPlayerHandler;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetParser;
import xyz.lilyflower.solaris.api.CustomDataLoader;
import xyz.lilyflower.solaris.util.reflect.ClasspathScanning;
import xyz.lilyflower.solaris.util.list.InvertedList;
import xyz.lilyflower.solaris.util.reflect.SolarisReflection;
import xyz.lilyflower.solaris.world.DimensionalWorldType;

@Mod(modid = "solaris", version = "3.0", dependencies = "before:GalacticraftCore;after:lotr")
public class Solaris {
    public static LoadStage STAGE = LoadStage.BOOTSTRAP;
    public static final Logger LOGGER = LogManager.getLogger("Solaris");

    private static <T> void __INIT_MODULE(Class<T> target) {
        List<Class<T>> modules = ClasspathScanning.implementations(target, false, false);
        modules.forEach(module -> {
            try {
                Constructor<?> constructor = module.getConstructor();
                Object instance = constructor.newInstance();
                LOGGER.info("Loading data module {}", module.getName());
                Method method = target.getDeclaredMethod("init");
                method.setAccessible(true);
                method.invoke(instance);
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException exception) {
                LoggingHelper.oopsie(LOGGER, "CRITICAL DATA MODULE ERROR: " + module.getName(), exception);
            }
        });
    }

    @EventHandler
    public void construction(FMLConstructionEvent event) {
        STAGE = LoadStage.BOOTSTRAP;
        __INIT_MODULE(CustomDataLoader.class);
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        STAGE = LoadStage.PRELOADER;
        __INIT_MODULE(ConfigurationModule.class);
        SolarisConfigurationLoader.load(new File("config/solaris/"));
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();

        EventBus bus = FMLCommonHandler.instance().bus();
        bus.register(new SolarisPlayerHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        STAGE = LoadStage.RUNNING;
        if (SolarisLOTR.FIX_ORE_DICTIONARY && Loader.isModLoaded("lotr")) {
            OreDictionary.registerOre("dustSulfur", LOTRMod.sulfur);
            OreDictionary.registerOre("ingotMithril", LOTRMod.mithril);
            OreDictionary.registerOre("oreMithril", LOTRMod.oreMithril);
            OreDictionary.registerOre("nuggetMithril", LOTRMod.mithrilNugget);
        }

        if (Loader.isModLoaded("GalacticraftCore")) { SolarisIntegrationModule.add(new PlanetParser(), true); }
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        STAGE = LoadStage.FINALIZE;
        if (Loader.isModLoaded("lotr")) {
            SolarisLOTR.registerModdedWeapons();
            LOTRTime.DAY_LENGTH = (int) (SolarisLOTR.TIME_BASE * SolarisLOTR.TIME_MULTIPLIER);
        }

        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();

        if (Loader.isModLoaded("tropicraft")) {
            InvertedList<String> list = new InvertedList<>();
            List<String> old = SolarisReflection.get("net.tropicraft.config.ConfigMisc", "coconutBombWhitelistedUsers");
            list.addAll(old);
            SolarisReflection.set("net.tropicraft.config.ConfigMisc", "coconutBombWhitelistedUsers", list);
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        STAGE = LoadStage.SPINUP;
        if (Loader.isModLoaded("lotr")) event.registerServerCommand(new LTRDebuggerCommand());
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();
    }
}
