package xyz.lilyflower.solaris.configuration.modules;

import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

public class SolarisMW implements ConfigurationModule {
    public static boolean ALLOW_SHOOTING_WHEN_SPRINTING = true;

    @Override
    public void init() {
        SolarisConfigurationLoader.add("mw", configuration -> {
            ALLOW_SHOOTING_WHEN_SPRINTING = configuration.getBoolean("allowSprintFiring", "vics", true, "Allow shooting guns when sprinting");
        });
    }
}
