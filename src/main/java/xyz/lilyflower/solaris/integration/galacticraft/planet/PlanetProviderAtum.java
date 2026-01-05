package xyz.lilyflower.solaris.integration.galacticraft.planet;

import com.teammetallurgy.atum.world.AtumWorldProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeBalloons;

@SuppressWarnings("unused")
public class PlanetProviderAtum extends AtumWorldProvider implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return PlanetProvider.CreatePlanet(
                PlanetRegistrationHook.PRIMARY_SYSTEM,
                "atum",
                32766,
                35F,
                PlanetRegistrationHook.PRIMARY_SYSTEM == GalacticraftCore.solarSystemSol ? 8F : 1.375F,
                1.0F,
                1.45F,
                PlanetProvider.GCBodyIcon("saturn"),
                this.getClass(),
                IAtmosphericGas.OXYGEN,
                IAtmosphericGas.CO2,
                IAtmosphericGas.METHANE
        );
    }

    @Override
    public ITeleportType entryMethod() {
        return new TeleportTypeBalloons();
    }
}
