package xyz.lilyflower.solaris.configuration.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

public class SolarisGalacticraft implements ConfigurationModule {
    public static String MAIN_SOLAR_SYSTEM = "sol";
    public static boolean DISABLE_UNREACHABLE_PLANETS = false;
    public static List<String > DISABLED_CELESTIAL_BODIES = new ArrayList<>();
    public static float ENTRY_POD_SPEED = -0.5F;

    @Override
    public void init() {
        SolarisConfigurationLoader.add("GalacticraftCore", "galacticraft", configuration -> {
            DISABLE_UNREACHABLE_PLANETS = configuration.getBoolean("disableUnreachablePlanets", "galacticraft", false, "Disables the creation of unreachable planets. Useful to avoid clutter.");
            MAIN_SOLAR_SYSTEM = configuration.getString("mainSolarSystem", "galacticraft", "sol", "Main solar system. Change this if you disable Sol, or want to set the default galaxy map viewpoint.");
            DISABLED_CELESTIAL_BODIES = Arrays.asList(configuration.getStringList("disabledBodies", "galacticraft", new String[]{}, "List of celestial body IDs to disable."));
            ENTRY_POD_SPEED = configuration.getFloat("entryPodSpeed", "galacticraft", -0.5F, Float.MIN_VALUE, Float.MAX_VALUE, "Initial entry pod speed.");
            String[] data = configuration.getStringList("additionalPlanets", "galacticraft", new String[]{}, "List of mods to register Galacticraft integration for.");
            for (String entry : data) {
                String[] split = entry.split(":");
                if (split.length >= 7) {

                }
            }
        });
    }
}
