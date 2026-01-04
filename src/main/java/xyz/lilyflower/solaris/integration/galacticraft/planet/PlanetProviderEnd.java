package xyz.lilyflower.solaris.integration.galacticraft.planet;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.world.WorldProviderEnd;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeBalloons;

public class PlanetProviderEnd extends WorldProviderEnd implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        return null;
    }

    @Override
    public ITeleportType entryMethod() {
        return new TeleportTypeBalloons(); // lol. lmao even
    }
}
