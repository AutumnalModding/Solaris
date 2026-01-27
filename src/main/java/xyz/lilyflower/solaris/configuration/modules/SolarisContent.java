package xyz.lilyflower.solaris.configuration.modules;

import java.util.LinkedHashMap;
import java.util.Map;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.data.TriPair;
import xyz.lilyflower.solaris.world.DimensionalWorldType;

@SuppressWarnings("unused")
public class SolarisContent implements ConfigurationModule {
    public static boolean ENABLE_CONTENT = false;
    public static String MODPACK_IDENTIFIER = "";
    public static final Map<Integer, TriPair<Double, Double, Double>> COORDINATES = new LinkedHashMap<>();

    @Override
    public void init() {
        SolarisConfigurationLoader.add("solaris", "content", configuration -> {
            ENABLE_CONTENT = configuration.getBoolean("globalContentToggle", "content", false, "Enables or disables all Solaris content. You probably want to leave this disabled.");
            MODPACK_IDENTIFIER = configuration.getString("modpackIdentifier", "content", "", "Modpack identifier for internal content. Don't touch this unless you know what you're doing.");

            String[] dimensions = configuration.getStringList("dimensionalWorldTypes", "content", new String[]{}, "List of dimensions to register world types for. Size must be the same as or bigger than dimensionalTeleportCoordinates!");
            String[] coordinates = configuration.getStringList("dimensionalTeleportCoordinates", "content", new String[]{}, "Use with dimensionalWorldTypes to teleport players to specific locations.");

            for (String dimension : dimensions) {
                int target = Integer.parseInt(dimension);
                Solaris.LOGGER.info("Adding dimensional world type for dimension {}...", target);
                DimensionalWorldType.create(target);
            }

            for (int index = 0; index < coordinates.length; index++) {
                String tuple = coordinates[index];
                String[] split = tuple.split(" ");
                if (split.length != 3) {
                    Solaris.LOGGER.warn("Coordinate pair '{}' was not exactly three long!", tuple);
                } else {
                    double x = Double.parseDouble(split[0]);
                    double y = Double.parseDouble(split[1]);
                    double z = Double.parseDouble(split[2]);
                    COORDINATES.put(Integer.parseInt(dimensions[index]), new TriPair<>(x, y, z));
                }
            }
        });
    }
}
