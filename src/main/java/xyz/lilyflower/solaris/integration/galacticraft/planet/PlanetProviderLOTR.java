package xyz.lilyflower.solaris.integration.galacticraft.planet;

import lotr.common.world.LOTRWorldProviderMiddleEarth;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import xyz.lilyflower.solaris.api.PlanetProvider;

public class PlanetProviderLOTR extends LOTRWorldProviderMiddleEarth implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return null;
    }
}
