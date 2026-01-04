package xyz.lilyflower.solaris.configuration.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraftforge.common.config.Configuration;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

@SuppressWarnings("unused")
public final class SolarisVanilla implements ConfigurationModule {
    public static boolean NO_IFRAME_PROJECTILES = false;
    public static ArrayList<String> NO_IFRAME_DAMAGETYPES;
    public static boolean DISABLE_WORLDGEN_SPAWNING = false;
    public static boolean DISABLE_SNOW_UPDATES = false;
    public static int END_PORTAL_TARGET = 1;

    public static final Consumer<Configuration> COMBAT_TWEAKS = configuration -> {
        NO_IFRAME_DAMAGETYPES = new ArrayList<>(Arrays.asList(configuration.getStringList("noImmunityDamageTypes", "vanilla.damage", new String[]{},
                "List of damage sources for which iframes aren't applied."
        )));

        NO_IFRAME_PROJECTILES = configuration.getBoolean("noImmunityForProjectiles", "vanilla.damage", false, "Make projectiles ignore iframes.");
    };

    public static final Consumer<Configuration> BANDAID_FIXES = configuration -> {
        DISABLE_SNOW_UPDATES = configuration.getBoolean("disableSnowUpdates", "bandaid", false, "Disables snow sheet blocks from sending neighbour updates.\nCan stop StackOverflowExceptions in some cases.");
        DISABLE_WORLDGEN_SPAWNING = configuration.getBoolean("disableWorldgenSpawning", "bandaid", false, "Disables animals spawning during worldgen.\nCan fix 'this.entitiesByUuid is null' crashes during world creation.");
    };

    public static final Consumer<Configuration> MISC_TWEAKS = configuration -> {
        END_PORTAL_TARGET = configuration.getInt("endPortalTarget", "vanilla.misc", 1, Integer.MIN_VALUE, Integer.MAX_VALUE, "Target for End Portal blocks to send you to when exiting the End.");
    };

    public void init() {
        SolarisConfigurationLoader.add("solaris", MISC_TWEAKS);
        SolarisConfigurationLoader.add("solaris", COMBAT_TWEAKS);
        SolarisConfigurationLoader.add("solaris", BANDAID_FIXES);
    }
}
