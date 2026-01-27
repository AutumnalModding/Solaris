package xyz.lilyflower.solaris.api;

import cpw.mods.fml.common.Loader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import xyz.lilyflower.solaris.init.Solaris;

public interface SolarisIntegrationModule {
    Map<SolarisIntegrationModule, Boolean> LOADERS = new HashMap<>();

    void run();
    List<String> requiredMods();
    boolean valid();

    static void add(SolarisIntegrationModule loader, boolean enabled) {
        LOADERS.put(loader, enabled);
    }

    static void execute() {
        LOADERS.forEach((loader, enabled) -> {
            StringBuilder name = new StringBuilder();
            boolean valid = loader.valid();
            for (String mod : loader.requiredMods()) { // Won't work in FMLConstruction
                if (!Loader.isModLoaded(mod) && Solaris.STAGE != LoadStage.BOOTSTRAP) {
                    valid = false;
                    break;
                } else {
                    name.append(mod).append("-");
                }
            }
            if (valid && enabled) {
                String integration = name.substring(0, name.length() - 1);
                Solaris.LOGGER.info("Enabling {} integration!", integration);
                loader.run();
            }
        });
    }
}
