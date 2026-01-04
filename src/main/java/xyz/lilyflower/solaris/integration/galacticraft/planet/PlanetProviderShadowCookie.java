package xyz.lilyflower.solaris.integration.galacticraft.planet;

import jajo_11.ShadowWorld.World.WorldProviderCookie;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import xyz.lilyflower.solaris.api.PlanetProvider;

public class PlanetProviderShadowCookie extends WorldProviderCookie implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return null;
    }
}
