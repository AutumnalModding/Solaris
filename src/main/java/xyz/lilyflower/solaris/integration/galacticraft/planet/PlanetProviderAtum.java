package xyz.lilyflower.solaris.integration.galacticraft.planet;

import com.teammetallurgy.atum.world.AtumWorldProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeBalloons;
import xyz.lilyflower.solaris.internal.illumos.PlanetSetup;

public class PlanetProviderAtum extends AtumWorldProvider implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return null;
    }

    @Override
    public ITeleportType entryMethod() {
        return new TeleportTypeBalloons();
    }
}
