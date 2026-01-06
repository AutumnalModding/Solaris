package xyz.lilyflower.solaris.configuration.modules;

import com.emoniph.witchery.util.IHandleDT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;
import xyz.lilyflower.solaris.init.Solaris;

@SuppressWarnings({"unchecked", "unused"})
public class SolarisWitchery implements ConfigurationModule {
    public static final HashMap<Class<? extends IHandleDT>, Float> WITCHERY_DAMAGE_CAPS = new HashMap<>();
    public static List<String> SAFE_LOTR_BIOMES;
    public static final List<Integer> ALLOWED_RITUAL_DIMENSIONS = new ArrayList<>();

    public static boolean isBiomeSafe(String biome) {
        return SAFE_LOTR_BIOMES.contains(biome);
    }

    public void init() {
        SolarisConfigurationLoader.add("witchery", "witchery", configuration -> {
            String[] caps = configuration.getStringList("witcheryDamageCaps", "witchery", new String[]{},
                    """
                            Class-cap mapping for Witchery bosses. Format: <EntityName>:<DamageCap>
                            Use -1 for no cap. Valid entity names:\s
                              BabaYaga,\s
                              Death,\s
                              GoblinGulg,\s
                              GoblinMog,\s
                              HornedHuntsman,\s
                              Leonard,\s
                              Lilith,\s
                              LordOfTorment,\s
                              Reflection,\s
                              Vampire"""
            );

            for (String cap : caps) {
                String[] split = cap.split(":");
                String classname = "com.emoniph.witchery.entity.Entity" + split[0];
                float value = Float.parseFloat(split[1]);

                try {
                    // all witchery bosses implement this
                    WITCHERY_DAMAGE_CAPS.put((Class<? extends IHandleDT>) Class.forName(classname), value < 0 ? Float.MAX_VALUE : value);
                } catch (NoClassDefFoundError | ClassNotFoundException exception) {
                    Solaris.LOGGER.error("Could not find class: {}!", classname);
                }
            }

            for (String dimension : configuration.getStringList("allowedRitualDimensions", "witchery", new String[]{"0"}, "Allowed dimensions for Witchery's vampire ritual.")) {
                try {
                    ALLOWED_RITUAL_DIMENSIONS.add(Integer.parseInt(dimension));
                } catch (NumberFormatException exception) {
                    Solaris.LOGGER.error("Failed to parse dimension ID: {}!", dimension);
                }
            }
        });
    }
}
