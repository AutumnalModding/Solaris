package xyz.lilyflower.solaris.internal.illumos;

import com.teammetallurgy.atum.world.AtumWorldProvider;
import java.util.Arrays;
import java.util.List;
import lotr.common.world.LOTRWorldProviderMiddleEarth;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOverworld;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars;
import net.aetherteam.aether.worldgen.WorldProviderAether;
import net.minecraft.util.ResourceLocation;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.integration.galacticraft.StarRegistry;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeBalloons;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeDropPod;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public class PlanetSetup implements SolarisIntegrationModule {
    public static final SolarSystem ORACLE = new SolarSystem("oracle", "milkyWay")
            .setMapPosition(new Vector3(0, 0, 0));

    public static final Star EYE_OF_VELZIE = (Star) new Star("eov")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(2)
            .setBodyIcon(ICON("neptune"))
            .atmosphereComponent(IAtmosphericGas.OXYGEN)
            .atmosphereComponent(IAtmosphericGas.HELIUM)
            .atmosphereComponent(IAtmosphericGas.HYDROGEN)
            .atmosphereComponent(IAtmosphericGas.NITROGEN)
            .setDimensionInfo(32765, AetherPlanetProvider.class)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.0F, 0.0F));

    public static final Planet ARDA = (Planet) new Planet("arda")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(1)
            .setRelativeOrbitTime(2.0F)
            .setBodyIcon(ICON("earth"))
            .atmosphereComponent(IAtmosphericGas.ARGON)
            .atmosphereComponent(IAtmosphericGas.OXYGEN)
            .atmosphereComponent(IAtmosphericGas.WATER)
            .atmosphereComponent(IAtmosphericGas.NITROGEN)
            .setDimensionInfo(32767, MiddleEarthPlanetProvider.class);

    public static final Planet ERIS = (Planet) new Planet("eris")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(1)
            .setRelativeOrbitTime(20.0F)
            .setBodyIcon(ICON("moon"))
            .setDimensionInfo(-28, WorldProviderMoon.class)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.15F, 1.15F));

    public static final Planet KV62 = (Planet) new Planet("KV62")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(1)
            .setRelativeSize(1.45F)
            .setRelativeOrbitTime(35.0F)
            .setBodyIcon(ICON("venus"))
            .atmosphereComponent(IAtmosphericGas.CO2)
            .atmosphereComponent(IAtmosphericGas.OXYGEN)
            .atmosphereComponent(IAtmosphericGas.METHANE)
            .setDimensionInfo(32766, AtumPlanetProvider.class)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.375F, 1.375F));

    public static final Planet CYALUME_BELT = (Planet) new Planet("cyalume")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(3)
            .setRelativeOrbitTime(45.0F)
            .setBodyIcon(ICON("asteroid"))
            .setPhaseShift(SolarisExtensions.TAU) // 2π
            .setDimensionInfo(-30, WorldProviderAsteroids.class)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.75F, 1.75F));

    // Yes, I know this one's a star. No, I don't care lmao
    public static final Planet EPSILON_ERIDANI = (Planet) new Planet("eridani")
            .setParentSolarSystem(ORACLE)
            .setTierRequired(2)
            .setPhaseShift(0.1667F)
            .setRelativeSize(0.535F)
            .setRelativeOrbitTime(1.95F)
            .setBodyIcon(ICON("mars"))
            .atmosphereComponent(IAtmosphericGas.CO2)
            .atmosphereComponent(IAtmosphericGas.ARGON)
            .atmosphereComponent(IAtmosphericGas.NITROGEN)
            .setDimensionInfo(-29, WorldProviderMars.class)
            .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.25F, 1.25F));

    @Override
    public void run() {
        Solaris.LOGGER.info("Registering Galacticraft planets...");
        StarRegistry.STARS.add(EYE_OF_VELZIE);
        ORACLE.setMainStar(EYE_OF_VELZIE);
        GalaxyRegistry.registerSolarSystem(ORACLE);
        GalaxyRegistry.registerPlanet(ARDA);
        GalaxyRegistry.registerPlanet(ERIS);
        GalaxyRegistry.registerPlanet(KV62);
        GalaxyRegistry.registerPlanet(CYALUME_BELT);
        GalaxyRegistry.registerPlanet(EPSILON_ERIDANI);

        GalacticraftRegistry.registerTeleportType(AtumWorldProvider.class, new TeleportTypeBalloons());
        GalacticraftRegistry.registerTeleportType(WorldProviderAether.class, new TeleportTypeDropPod());
        GalacticraftRegistry.registerTeleportType(LOTRWorldProviderMiddleEarth.class, new TeleportTypeOverworld());

        GalacticraftRegistry.registerTeleportType(AtumPlanetProvider.class, new TeleportTypeBalloons());
        GalacticraftRegistry.registerTeleportType(AetherPlanetProvider.class, new TeleportTypeDropPod());
        GalacticraftRegistry.registerTeleportType(MiddleEarthPlanetProvider.class, new TeleportTypeOverworld());

        GalacticraftRegistry.registerRocketGui(AtumWorldProvider.class, new ResourceLocation("galacticraftmars", "textures/gui/marsRocketGui.png"));
        GalacticraftRegistry.registerRocketGui(WorldProviderAether.class, new ResourceLocation("solaris", "textures/gui/celestial/aetherRocketGui.png"));
        GalacticraftRegistry.registerRocketGui(LOTRWorldProviderMiddleEarth.class, new ResourceLocation("galacticraftcore", "textures/gui/overworldRocketGui.png"));
    }

    private static ResourceLocation ICON(String body) {
        return new ResourceLocation("galacticraftcore", "textures/gui/celestialbodies/" + body + ".png");
    }

    @Override
    public List<String> requiredMods() {
        return Arrays.asList("GalacticraftCore", "lotr");
    }

    @Override
    public boolean valid() {
        return Solaris.STATE == LoadStage.RUNNING;
    }
}
