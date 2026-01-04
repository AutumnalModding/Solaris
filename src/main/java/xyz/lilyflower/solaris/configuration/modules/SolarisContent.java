package xyz.lilyflower.solaris.configuration.modules;

import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

public class SolarisContent implements ConfigurationModule {
    public static boolean ENABLE_CONTENT = false;
    public static String MODPACK_IDENTIFIER = "";

    @Override
    public void init() {
        SolarisConfigurationLoader.add("solaris", configuration -> {
            ENABLE_CONTENT = configuration.getBoolean("globalContentToggle", "content", false, "Enables or disables all Solaris content. You probably want to leave this disabled.");
            MODPACK_IDENTIFIER = configuration.getString("modpackIdentifier", "content", "", "Modpack identifier for internal content. Don't touch this unless you know what you're doing.");
        });
    }
}
