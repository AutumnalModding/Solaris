package xyz.lilyflower.solaris.integration.galacticraft.planet;

import jajo_11.ShadowWorld.World.WorldProviderShadow;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import xyz.lilyflower.solaris.api.PlanetProvider;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeDropPod;

public class PlanetProviderShadowWorld extends WorldProviderShadow implements PlanetProvider {
    @Override public float getThermalLevelModifier() { return -2; }
    @Override public double getSolarEnergyMultiplier() { return -1; }
    @Override public CelestialBody getCelestialBody() { return null; }
    @Override public ITeleportType entryMethod() {return new TeleportTypeDropPod(); }
}
