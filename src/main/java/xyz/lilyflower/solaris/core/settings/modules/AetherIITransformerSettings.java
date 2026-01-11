package xyz.lilyflower.solaris.core.settings.modules;

import xyz.lilyflower.solaris.api.TransformerSettingsModule;
import xyz.lilyflower.solaris.core.settings.SolarisTransformerSettings;

@SuppressWarnings("unused")
public class AetherIITransformerSettings implements TransformerSettingsModule {
    public static int FREEFALL_TARGET = 0;

    public void init() {
        SolarisTransformerSettings.add("aetherii", configuration -> {
            FREEFALL_TARGET = configuration.getInt("freefallDimensionTarget", "aetherii", 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Dimension ID for falling out of the Aether.");
        });
    }
}
