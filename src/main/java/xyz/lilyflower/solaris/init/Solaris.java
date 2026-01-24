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
import xyz.lilyflower.solaris.configuration.modules.SolarisLOTR;
import xyz.lilyflower.solaris.content.SolarisRegistryLoader;
import xyz.lilyflower.solaris.command.LTRDebuggerCommand;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetParser;
import xyz.lilyflower.solaris.api.CustomDataLoader;
import xyz.lilyflower.solaris.util.ClasspathScanning;
import xyz.lilyflower.solaris.util.InvertedList;

@Mod(modid = "solaris", version = "3.0", dependencies = "before:GalacticraftCore;after:lotr")
public class Solaris {
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
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        STATE = LoadStage.PRELOADER;
        __INIT_MODULE(ConfigurationModule.class);
        SolarisConfigurationLoader.load(new File("config/solaris/"));
        SolarisRegistryLoader.initialize();
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

        SolarisRegistryLoader.initialize();
        if (Loader.isModLoaded("GalacticraftCore")) { SolarisIntegrationModule.add(new PlanetParser(), true); }
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

                ArrayList<ItemStack> items = new ArrayList<>();
                ArrayList<ItemStack> blocks = new ArrayList<>();

                for (Object obj : Item.itemRegistry) {
                    Item item = (Item) obj;
                    try {
                        item.getSubItems(item, null, items);
                    } catch (Exception exception) {
                        ItemStack stack = new ItemStack(item, 0);
                        LOGGER.warn("Failed getting variants for item {} (\"{}\") of type {}! (Reason: {}: {})",
                                item.getUnlocalizedName(),
                                item.getItemStackDisplayName(stack),
                                item.getClass().getName(),
                                exception.getClass().getName(),
                                exception.getMessage()
                        );
                        items.add(stack);
                    }
                }

                for (Object obj : Block.blockRegistry) {
                    Block block = (Block) obj;
                    Item item = Item.getItemFromBlock(block);
                    try {
                        if (item != null) {
                            item.getSubItems(item, null, blocks);
                        }
                    } catch (Exception exception) {
                        LOGGER.warn("Failed getting variants for block {} (\"{}\") of type {}! (Reason: {}: {})",
                                block.getUnlocalizedName(),
                                block.getLocalizedName(),
                                block.getClass().getName(),
                                exception.getClass().getName(),
                                exception.getMessage()
                        );
                        blocks.add(new ItemStack(item, 0));
                    }
                }

                list.addAll(items);
                list.addAll(blocks);

                possible.set(null, list);
                Solaris.LOGGER.info("Found {} items, {} blocks. {} possible Continuum Orb items. Happy gambling!", items.size(), blocks.size(), list.size());
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();

        if (Loader.isModLoaded("tropicraft")) {
            try {
                Class<?> that = Class.forName("net.tropicraft.config.ConfigMisc");
                Field field = that.getDeclaredField("coconutBombWhitelistedUsers");
                field.setAccessible(true);
                InvertedList<String> list = new InvertedList<>();
                List<String> old = (List<String>) field.get(null);
                list.addAll(old);
                field.set(null, list);
            } catch (ReflectiveOperationException exception) {
                LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "Failed to invert Tropicraft's coconut bomb whitelist.", exception);
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        STATE = LoadStage.SPINUP;
        if (Loader.isModLoaded("lotr")) event.registerServerCommand(new LTRDebuggerCommand());
        SolarisRegistryLoader.initialize();
        SolarisIntegrationModule.execute();
    }
}
