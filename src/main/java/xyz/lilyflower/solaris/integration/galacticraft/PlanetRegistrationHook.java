package xyz.lilyflower.solaris.integration.galacticraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.init.Solaris;

public class PlanetRegistrationHook implements SolarisIntegrationModule {
    public static final HashSet<Star> STARS = new HashSet<>();
    public static SolarSystem ALPHA = !SolarisGalacticraft.DISABLED_CELESTIAL_BODIES.contains("sol")
            ? GalacticraftCore.solarSystemSol
            : new SolarSystem("solaris$alpha", "milkyWay").setMapPosition(new Vector3(0, 0, 0));

    public static SolarSystem BETA = new SolarSystem("solaris$beta", "milkyWay").setMapPosition(new Vector3(-0.55F, 1.0F, 0.0F));

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        GalaxyRegistry.registerSolarSystem(ALPHA);
        GalaxyRegistry.registerSolarSystem(BETA);

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
    }

    @Override public boolean valid() { return Solaris.STATE == LoadStage.RUNNING; }
    @Override public List<String> requiredMods() { return Arrays.asList("GalacticraftCore"); }
}
