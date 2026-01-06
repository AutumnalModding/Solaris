package xyz.lilyflower.solaris.configuration.modules;

import java.util.List;
import java.util.Arrays;
import xyz.lilyflower.solaris.api.ConfigurationModule;
import xyz.lilyflower.solaris.configuration.SolarisConfigurationLoader;

public class SolarisQuiverbow implements ConfigurationModule {
    public static List<String> ENTITY_BLACKLIST;

    @Override
    public void init() {
        SolarisConfigurationLoader.add("quiverchevsky", "quiverbow", configuration -> {
            ENTITY_BLACKLIST = Arrays.asList(configuration.getStringList("soulCairnBlacklist", "quiverbow", new String[]{"net.minecraft.entity.boss.IBossDisplayData"}, "List of entities that are barred from the Soul Cairn. Uses fully qualified class names."));
        });
    }
}
