package xyz.lilyflower.solaris.configuration.modules;

import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

public class SolarisAether implements ConfigurationModule {
    public static boolean CONTINUUM_DANGEROUS = false;

    @Override
    public void init() {
        SolarisConfigurationLoader.add("aether", "aetherii", configuration -> { // no way to know WHICH aether is loaded
            CONTINUUM_DANGEROUS = configuration.getBoolean("dangerousContinuumOrbs", "aetherii", false, "Allows Continuum Orbs to spawn ANY block and item in the game. WARNING: POTENTIALLY DANGEROUS!");
        });
    }
}
