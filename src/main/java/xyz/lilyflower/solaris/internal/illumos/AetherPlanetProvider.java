package xyz.lilyflower.solaris.internal.illumos;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import net.aetherteam.aether.worldgen.WorldProviderAether;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetProvider;

public class AetherPlanetProvider extends WorldProviderAether implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return PlanetSetup.EYE_OF_VELZIE;
    }

    @Override
    public double getSolarEnergyMultiplier() {
        return 3.75D;
    }

    @Override
    public double getFuelUsageMultiplier() {
        return 0.45D;
    }
}
