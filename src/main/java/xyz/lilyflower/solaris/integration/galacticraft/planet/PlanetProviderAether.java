package xyz.lilyflower.solaris.integration.galacticraft.planet;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.aetherteam.aether.worldgen.WorldProviderAether;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeDropPod;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeLander;
import xyz.lilyflower.solaris.internal.illumos.PlanetSetup;

public class PlanetProviderAether extends WorldProviderAether implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return null;
    }

    @Override
    public double getSolarEnergyMultiplier() {
        return 3.75D;
    }

    @Override
    public double getFuelUsageMultiplier() {
        return 0.45D;
    }

    @Override
    public ITeleportType entryMethod() {
        return new TeleportTypeDropPod();
    }
}
