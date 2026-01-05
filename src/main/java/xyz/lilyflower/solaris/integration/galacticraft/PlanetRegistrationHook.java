package xyz.lilyflower.solaris.integration.galacticraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.init.Solaris;

public class PlanetRegistrationHook implements SolarisIntegrationModule {
    public static SolarSystem PRIMARY_SYSTEM = null;
    public static SolarSystem SECONDARY_SYSTEM = new SolarSystem("solaris_secondary", "milkyWay");

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        for (int index = 0; index < SolarisGalacticraft.MODDED_PLANET_INTEGRATION.size(); index++) {
            String name = "xyz.lilyflower.solaris.integration.galacticraft.planet.PlanetProvider" + SolarisGalacticraft.MODDED_PLANET_INTEGRATION.get(index);
            if (name.equals("xyz.lilyflower.solaris.integration.galacticraft.planet.PlanetProvider")) continue;
            try {
                Class<? extends PlanetProvider> provider = (Class<? extends PlanetProvider>) Class.forName(name);
                Constructor<? extends PlanetProvider> constructor = provider.getConstructor();
                PlanetProvider instance = constructor.newInstance();
                int tier = SolarisGalacticraft.INTEGRATION_TIERS.get(index);
                instance.register(tier);
                Solaris.LOGGER.info("Registering {} with tier {}...", name, tier);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                Solaris.LOGGER.warn("Invalid modded planet provider {}!", name);
            }
        }

        PRIMARY_SYSTEM = GalaxyRegistry.getRegisteredSolarSystems().get(SolarisGalacticraft.MAIN_SOLAR_SYSTEM);
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
