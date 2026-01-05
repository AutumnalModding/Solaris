package xyz.lilyflower.solaris.integration.galacticraft.planet;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import net.minecraft.world.WorldProviderEnd;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.PlanetRegistrationHook;

@SuppressWarnings("unused")
public class PlanetProviderEnd extends WorldProviderEnd implements PlanetProvider {
    @Override
    public CelestialBody getCelestialBody() {
        Star body = PlanetProvider.CreateStar(
                PlanetRegistrationHook.SECONDARY_SYSTEM,
                "the_end",
                32764,
                1.0F,
                0.0F,
                1.0F,
                2.0F,
                PlanetProvider.GCBodyIcon("black_hole"),
                this.getClass()
        );
        PlanetRegistrationHook.SECONDARY_SYSTEM.setMainStar(body);
        return body;
    }
}
