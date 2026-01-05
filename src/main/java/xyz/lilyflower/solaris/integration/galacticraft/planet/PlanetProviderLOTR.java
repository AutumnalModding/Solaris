package xyz.lilyflower.solaris.integration.galacticraft.planet;

import lotr.common.world.LOTRWorldProviderMiddleEarth;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;

@SuppressWarnings("unused")
public class PlanetProviderLOTR extends LOTRWorldProviderMiddleEarth implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return PlanetProvider.CreatePlanet(
                PlanetRegistrationHook.PRIMARY_SYSTEM,
                "lotr",
                32765,
                2F,
                PlanetRegistrationHook.PRIMARY_SYSTEM == GalacticraftCore.solarSystemSol ? 3.8F : 1F,
                1.0F,
                1.45F,
                PlanetProvider.GCBodyIcon("earth"),
                this.getClass(),
                IAtmosphericGas.OXYGEN,
                IAtmosphericGas.CO2,
                IAtmosphericGas.METHANE
        );
    }
}
