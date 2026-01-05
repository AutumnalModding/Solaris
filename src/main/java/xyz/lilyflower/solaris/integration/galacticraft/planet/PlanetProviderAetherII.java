package xyz.lilyflower.solaris.integration.galacticraft.planet;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.aetherteam.aether.worldgen.WorldProviderAether;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeDropPod;

@SuppressWarnings("unused")
public class PlanetProviderAetherII extends WorldProviderAether implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return PlanetProvider.CreateStar( // TODO: sol support
            PlanetRegistrationHook.PRIMARY_SYSTEM,
            "aether_ii",
            32767,
            1.0F,
            0.0F,
            1.0F,
            1.0F,
            PlanetProvider.GCBodyIcon("neptune"),
            this.getClass(),
            IAtmosphericGas.OXYGEN,
            IAtmosphericGas.HELIUM,
            IAtmosphericGas.HYDROGEN
        );
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
