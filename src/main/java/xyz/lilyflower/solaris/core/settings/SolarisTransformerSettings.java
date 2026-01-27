package xyz.lilyflower.solaris.core.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraftforge.common.config.Configuration;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.solaris.util.data.Pair;

@SuppressWarnings("unused")
public class SolarisTransformerSettings {
    private static final ArrayList<Pair<String, Consumer<Configuration>>> CONFIG_RUNNERS = new ArrayList<>();

    // No 'mod' parameter -- too early to be depending on that!
    public static void add(String identifier, Consumer<Configuration> runner) {
        CONFIG_RUNNERS.add(new Pair<>(identifier, runner));
    }

    public static void load() {
        // Make sure we don't load earlyconfig in a development environment - for some reason, it dies?
        if (System.getProperties().containsKey("net.minecraftforge.gradle.GradleStart.srgDir")) return;
        ArrayList<Configuration> files = new ArrayList<>();
        SolarisBootstrap.LOGGER.info("Initializing transformer settings...");

        CONFIG_RUNNERS.forEach(runner -> {
            Configuration settings = new Configuration(new File("config/solaris/early/" + runner.left() + ".cfg"));
            files.add(settings);
            SolarisBootstrap.LOGGER.info("Parsing settings for '{}'!", runner.left().toUpperCase());
            runner.right().accept(settings);
        });

        for (Configuration backing : files) if (backing.hasChanged()) backing.save();
    }
}
