package xyz.lilyflower.solaris.configuration.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.init.Solaris;

public class SolarisGalacticraft implements ConfigurationModule {
    public static String MAIN_SOLAR_SYSTEM = "sol";
    public static boolean DISABLE_UNREACHABLE_PLANETS = false;
    public static List<String > DISABLED_CELESTIAL_BODIES = new ArrayList<>();
    public static List<String> MODDED_PLANET_INTEGRATION = new ArrayList<>();
    public static List<Integer> INTEGRATION_TIERS = new ArrayList<>();
    public static float ENTRY_POD_SPEED = -2;

    @Override
    public void init() {
        SolarisConfigurationLoader.add("GalacticraftCore", configuration -> {
            DISABLE_UNREACHABLE_PLANETS = configuration.getBoolean("disableUnreachablePlanets", "galacticraft", false, "Disables the creation of unreachable planets. Useful to avoid clutter.");
            MODDED_PLANET_INTEGRATION = Arrays.asList(configuration.getStringList("additionalModdedPlanets", "galacticraft", new String[]{}, "List of mods to register Galacticraft integration for."));
            MAIN_SOLAR_SYSTEM = configuration.getString("mainSolarSystem", "galacticraft", "sol", "Main solar system. Change this if you disable Sol, or want to set the default galaxy map viewpoint.");
            DISABLED_CELESTIAL_BODIES = Arrays.asList(configuration.getStringList("disabledBodies", "galacticraft", new String[]{}, "List of celestial body IDs to disable."));
            ENTRY_POD_SPEED = configuration.getFloat("entryPodSpeed", "galacticraft", -2, Float.MIN_VALUE, 0, "Initial entry pod speed.");
            String[] tiers = configuration.getStringList("integrationTiers", "galacticraft", new String[]{}, "Tiers for modded planet integration. Both lists MUST be the same size! Index order matters.");
            for (String tier : tiers) {
                try {
                    int parsed = Integer.parseInt(tier);
                    INTEGRATION_TIERS.add(parsed);
                } catch (NumberFormatException exception) {
                    Solaris.LOGGER.error("Invalid tier {}!", tier);
                }
            }
        });
    }
}
