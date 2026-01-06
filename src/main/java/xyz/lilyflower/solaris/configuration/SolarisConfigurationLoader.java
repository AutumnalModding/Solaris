package xyz.lilyflower.solaris.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import cpw.mods.fml.common.Loader;
import java.util.function.Consumer;
import net.minecraftforge.common.config.Configuration;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public class SolarisConfigurationLoader {
    private static final HashMap<SolarisExtensions.Pair<String, String>, ArrayList<Consumer<Configuration>>> MODULES = new HashMap<>();

    public static void add(String mod, String name, Consumer<Configuration> module) {
        SolarisExtensions.Pair<String, String> pair = new SolarisExtensions.Pair<>(mod, name);
        ArrayList<Consumer<Configuration>> modules = MODULES.getOrDefault(pair, new ArrayList<>());
        modules.add(module);
        MODULES.put(pair, modules);
    }

    public static void load(File directory) {
        ArrayList<Configuration> files = new ArrayList<>();

        MODULES.forEach((pair, modules) -> {
            if (Loader.isModLoaded(pair.left())) {
                Configuration backing = new Configuration(new File(directory.getPath() + "/" + pair.right() + ".cfg"));
                for (Consumer<Configuration> module : modules) {
                    Solaris.LOGGER.debug("Loading configuration module '{}' for mod {}!", pair.right(), pair.left());
                    module.accept(backing);
                }
                files.add(backing);
            }
        });

        for (Configuration backing : files) if (backing.hasChanged()) backing.save();
    }
}
