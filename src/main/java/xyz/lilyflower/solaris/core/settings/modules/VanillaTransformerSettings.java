package xyz.lilyflower.solaris.core.settings.modules;

import java.util.HashMap;
import xyz.lilyflower.solaris.api.TransformerSettingsModule;
import xyz.lilyflower.solaris.core.settings.SolarisTransformerSettings;
import xyz.lilyflower.solaris.init.Solaris;

@SuppressWarnings("unused")
public class VanillaTransformerSettings implements TransformerSettingsModule {
    public static HashMap<String, Integer> PROVIDER_RESPAWNS = new HashMap<>();
    public static int DEFAULT_RESPAWN_DIMENSION = 0;

    @Override
    public void init() {
        SolarisTransformerSettings.add("vanilla", configuration -> {
            DEFAULT_RESPAWN_DIMENSION = configuration.getInt("defaultRespawnDimension", "vanilla", 0, Short.MIN_VALUE, Short.MAX_VALUE, "Default dimension for respawning.");

            String[] map = configuration.getStringList("worldProviderRespawnDimensions", "vanilla", new String[0], "Provider/Dimension map for respawning. Example: net/minecraft/world/WorldProviderEnd:-1");
            for (String entry : map) {
                String[] split = entry.split(":");
                try {
                    PROVIDER_RESPAWNS.put(split[0], Integer.parseInt(split[1]));
                } catch (ArrayIndexOutOfBoundsException bounds) {
                    Solaris.LOGGER.error("Invalid respawn dimension map entry: {}!", entry);
                } catch (NumberFormatException exception) {
                    Solaris.LOGGER.error("Invalid respawn dimension: {}!", split[1]);
                }
            }
        });
    }
}
