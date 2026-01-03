package xyz.lilyflower.solaris.init;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lotr.common.LOTRMod;
import lotr.common.LOTRTime;
import net.aetherteam.aether.items.consumables.ItemContinuum;
import net.aetherteam.aether.recipes.AetherRecipes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.configuration.modules.SolarisAether;
import xyz.lilyflower.solaris.configuration.modules.SolarisContent;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.configuration.modules.SolarisLOTR;
import xyz.lilyflower.solaris.content.SolarisRegistry;
import xyz.lilyflower.solaris.command.LTRDebuggerCommand;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;
import xyz.lilyflower.solaris.api.CustomDataLoader;
import xyz.lilyflower.solaris.internal.illumos.PlanetSetup;
import xyz.lilyflower.solaris.util.ClasspathScanning;

@Mod(modid = Solaris.MODID, version = Solaris.VERSION, dependencies = "before:GalacticraftCore;after:lotr")
public class Solaris {
    public static final String VERSION = "3.0";
    public static final String MODID = "solaris";
    public static LoadStage STATE = LoadStage.BOOTSTRAP;

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
        STATE = LoadStage.BOOTSTRAP;
        __INIT_MODULE(CustomDataLoader.class);
        SolarisRegistry.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        STATE = LoadStage.PRELOADER;
        __INIT_MODULE(ConfigurationModule.class);
        SolarisConfigurationLoader.load(new File("config/solaris.cfg"));
        SolarisRegistry.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        STATE = LoadStage.RUNNING;
        if (SolarisLOTR.FIX_ORE_DICTIONARY && Loader.isModLoaded("lotr")) {
            OreDictionary.registerOre("dustSulfur", LOTRMod.sulfur);
            OreDictionary.registerOre("ingotMithril", LOTRMod.mithril);
            OreDictionary.registerOre("oreMithril", LOTRMod.oreMithril);
            OreDictionary.registerOre("nuggetMithril", LOTRMod.mithrilNugget);
        }

        SolarisRegistry.initialize();
        if (Loader.isModLoaded("GalacticraftCore")) {
            SolarisIntegrationModule.add(new PlanetSetup(), SolarisContent.MODPACK_IDENTIFIER.equals("illumos"));
            SolarisIntegrationModule.add(new PlanetRegistrationHook(), !SolarisGalacticraft.MODDED_PLANET_INTEGRATION.isEmpty());
        }
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void postInit(FMLPostInitializationEvent event) {
        STATE = LoadStage.FINALIZE;
        if (Loader.isModLoaded("lotr")) {
            SolarisLOTR.registerModdedWeapons();
            LOTRTime.DAY_LENGTH = (int) (SolarisLOTR.TIME_BASE * SolarisLOTR.TIME_MULTIPLIER);
        }
        
        if (Loader.isModLoaded("aether") && SolarisAether.CONTINUUM_DANGEROUS) {
            try {
                Class<ItemContinuum> clazz = ItemContinuum.class;
                Field possible = clazz.getDeclaredField("possibleItems");
                possible.setAccessible(true);
                ArrayList<ItemStack> list = (ArrayList<ItemStack>) possible.get(null);
                list.clear();
                for (Object obj : Item.itemRegistry) {
                    list.add(new ItemStack((Item) obj));
                }

                for (Object obj : Block.blockRegistry) {
                    list.add(new ItemStack(Item.getItemFromBlock((Block) obj)));
                }

                possible.set(null, list);
                Solaris.LOGGER.info("Found {} items, {} blocks. {} possible Continuum Orb items. Happy gambling!", Item.itemRegistry.getKeys().size(), Block.blockRegistry.getKeys().size(), list.size());
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        SolarisRegistry.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        STATE = LoadStage.SPINUP;
        if (Loader.isModLoaded("lotr")) event.registerServerCommand(new LTRDebuggerCommand());
        SolarisRegistry.initialize();
        SolarisIntegrationModule.execute();
    }
}
