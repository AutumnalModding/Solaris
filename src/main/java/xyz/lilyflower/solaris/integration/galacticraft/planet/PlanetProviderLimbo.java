package xyz.lilyflower.solaris.integration.galacticraft.planet;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;
import org.dimdev.dimdoors.world.LimboProvider;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;
import xyz.lilyflower.solaris.integration.galacticraft.lander.TeleportTypeRandom;

@SuppressWarnings("unused")
public class PlanetProviderLimbo extends LimboProvider implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        Star body = (Star) PlanetProvider.CreateStar(
            PlanetRegistrationHook.BETA,
            "limbo",
            32764,
            1.0F,
            0.0F,
            1.0F,
            2.0F,
            PlanetProvider.GCBodyIcon("black_hole"),
            this.getClass()
        ).setForceStaticLoad(true);
        PlanetRegistrationHook.BETA.setMainStar(body);
        return body;
    }

    @Override
    public ITeleportType entryMethod() {
        return new TeleportTypeRandom(2500, 2500, EntityLandingBalloons.class);
    }
}
