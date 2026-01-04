package xyz.lilyflower.solaris.integration.galacticraft;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.init.Solaris;

public class PlanetRegistrationHook implements SolarisIntegrationModule {
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        for (String target : SolarisGalacticraft.MODDED_PLANET_INTEGRATION) {
            String pkg = "xyz.lilyflower.solaris.integration.galacticraft.planet.";
            try {
                Class<? extends PlanetProvider> provider = (Class<? extends PlanetProvider>) Class.forName(pkg + target);
                Constructor<? extends PlanetProvider> constructor = provider.getConstructor();
                PlanetProvider instance = constructor.newInstance();
                instance.register();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public List<String> requiredMods() {
        return Arrays.asList("GalacticraftCore");
    }

    @Override
    public boolean valid() {
        return Solaris.STATE == LoadStage.RUNNING;
    }
}
