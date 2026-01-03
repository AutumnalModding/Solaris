package xyz.lilyflower.solaris.configuration.modules;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lotr.common.fac.LOTRFaction;
import lotr.common.item.LOTRWeaponStats;
import lotr.common.world.map.LOTRWaypoint;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.init.Solaris;

@SuppressWarnings("unused")
public class SolarisLOTR implements ConfigurationModule {
    public static int TIME_BASE = 48000;
    public static float TIME_MULTIPLIER = 1;
    public static boolean OMNICIDE_MODE = false;
    public static List<String> GLOBAL_WAYPOINTS;
    public static List<String> DISABLED_WAYPOINTS;
    public static String[] ADDITIONAL_COMBAT_ITEMS;
    public static boolean UNLOCK_COSMETICS = true;
    public static boolean UNLOCK_WAYPOINTS = false;
    public static boolean FIX_ORE_DICTIONARY = true;
    public static boolean NO_WAYPOINT_LOCKING = false;
    public static boolean ENABLE_WANDERER_DEATH = false;
    public static boolean DISABLE_ATTACK_TIMINGS = false;
    public static boolean ALLOW_SCRAP_TRADER_SCREENSHOTS = false;
    public static boolean THERE_IS_NO_GENEVA_CONVENTION_IN_ARDA = false;

    public static final Consumer<Configuration> COMBAT_TWEAKS = configuration -> {
        DISABLE_ATTACK_TIMINGS = configuration.getBoolean("removeNewCombat", "lotr.combat", true, "Removes the 1.9-style combat changes that noone asked for.");
        ENABLE_WANDERER_DEATH = configuration.getBoolean("enableWandererDeath", "lotr.combat", false, "Binds the Grey Wanderer to this mortal coil, allowing him to be killed.");

        OMNICIDE_MODE = configuration.getBoolean("omnicideMode", "lotr.combat", false, "Makes all LOTR NPCs attack any mob, NPC or non-NPC.");
        THERE_IS_NO_GENEVA_CONVENTION_IN_ARDA = configuration.getBoolean("areGenevaConventionsMerelySuggestions", "lotr.combat", false, "Makes all factions approve of war crimes.");

        ADDITIONAL_COMBAT_ITEMS = configuration.getStringList("additionalCombatItems", "lotr.combat", new String[]{},
                """
                List of items to add to the custom LOTR combat system.
                Format: 'modid:item_name@speedMultiplier_reachMultiplier'
                Example: 'minecraft:golden_axe@1.5_1.0"""
        );

        if (THERE_IS_NO_GENEVA_CONVENTION_IN_ARDA) {
            for (LOTRFaction faction : LOTRFaction.values()) {
                faction.approvesWarCrimes = true;
            }
        }
    };

    public static final Consumer<Configuration> GENERIC_TWEAKS = configuration -> {
        UNLOCK_COSMETICS = configuration.getBoolean("unlockCosmetics", "lotr.misc", false, "Unlocks all player-specific LOTR cosmetics.");
        FIX_ORE_DICTIONARY = configuration.getBoolean("fixOreDictionary", "lotr.misc", true, "Registers LOTR ores to the Forge ore dictionary.");
        TIME_MULTIPLIER = configuration.getFloat("timeMultiplier", "lotr.misc", 1.0F, Float.MIN_VALUE, Float.MAX_VALUE, "Day length multiplier for LOTR days.");
        ALLOW_SCRAP_TRADER_SCREENSHOTS = configuration.getBoolean("allowOddmentScreenshots", "lotr.misc", false, "Allows to take screenshots of the Oddment Collector.");
        TIME_BASE = configuration.getInt("timeBase", "lotr.misc", 48000, 1, Integer.MAX_VALUE, "LOTR day length (base)");
    };

    public static final Consumer<Configuration> CROSS_MOD_INTEGRATION = configuration -> {
        SolarisWitchery.SAFE_LOTR_BIOMES = Arrays.asList(configuration.getStringList("safeBiomes", "lotr.integration", new String[]{}, "Safe LOTR biomes for vampires. Use display names, like 'Mordor' or 'Gorgoroth'."));
    };

    public static final Consumer<Configuration> FAST_TRAVEL_TWEAKS = configuration -> {
        DISABLED_WAYPOINTS = Arrays.asList(configuration.getStringList("disabledWaypoints", "lotr.travel", new String[]{},
                """
                List of waypoints to disable.
                Format: Internal waypoint name - run `/ltdebug dumpWaypoints` for a list.
                Example: 'MORANNON' would disable the Black Gate waypoint (display names and internal names often do not match!)"""
        ));

        GLOBAL_WAYPOINTS = Arrays.asList(configuration.getStringList("globalWaypoints", "lotr.travel", new String[]{},
                """
                List of waypoints to globally enable.
                Same format as disabledWaypoints."""
        ));

        UNLOCK_WAYPOINTS = configuration.getBoolean("unlockAllWaypoints", "lotr.travel", false, "Unlocks all fast travel waypoints.");
        NO_WAYPOINT_LOCKING = configuration.getBoolean("disableWaypointLocking", "lotr.travel", false, "Disables alignment-based waypoint locking.");
    };

    public static boolean isWaypointDisabled(LOTRWaypoint waypoint) {
        try {
            return DISABLED_WAYPOINTS.contains(waypoint.name());
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isWaypointGlobal(LOTRWaypoint waypoint) {
        try {
            return GLOBAL_WAYPOINTS.contains(waypoint.name());
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void registerModdedWeapons() {
        for (String entry : ADDITIONAL_COMBAT_ITEMS) {
            float speed = Float.parseFloat(entry.replaceAll(".*@", "").replaceAll("_.*", ""));
            float reach = Float.parseFloat(entry.replaceAll(".*_", ""));

            String modID = entry.replaceAll(":.*", "");
            String itemID = entry.replaceAll(".*:", "").replaceAll("@.*", "");

            Item item = GameRegistry.findItem(modID, itemID);

            if (item != null) {
                Solaris.LOGGER.debug("Registering item '{}:{}' to the LOTR combat system (speed {}x, reach {}x)...", modID, itemID, speed, reach);

                LOTRWeaponStats.registerMeleeSpeed(item, speed);
                LOTRWeaponStats.registerMeleeReach(item, reach);
            }
        }
    }

    public void init() {
        SolarisConfigurationLoader.add("lotr", COMBAT_TWEAKS);
        SolarisConfigurationLoader.add("lotr", GENERIC_TWEAKS);
        SolarisConfigurationLoader.add("lotr", CROSS_MOD_INTEGRATION);
        SolarisConfigurationLoader.add("lotr", FAST_TRAVEL_TWEAKS);
    }
}
