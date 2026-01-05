package xyz.lilyflower.solaris.api;

import cpw.mods.fml.common.Loader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.integration.galacticraft.StarRegistry;
import xyz.lilyflower.solaris.integration.galacticraft.TeleportTypeLander;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public interface PlanetProvider extends IGalacticraftWorldProvider, IExitHeight, ISolarLevel {
    HashMap<String, CelestialBody> CACHE = new HashMap<>();

    static Moon CreateMoon(
            Planet owner,
            String name, int dimension,
            float orbit, float distance,
            float shift, float size,
            ResourceLocation icon,
            Class<? extends WorldProvider> provider,
            IAtmosphericGas... atmosphere
    ) {
        return (Moon) CreateBody(new SolarisExtensions.TriPair<>(null, null, owner), name, dimension, orbit, distance, shift, size, Moon.class, icon, provider, atmosphere);
    }

    static Planet CreatePlanet(
            SolarSystem system,
            String name, int dimension,
            float orbit, float distance,
            float shift, float size,
            ResourceLocation icon,
            Class<? extends WorldProvider> provider,
            IAtmosphericGas... atmosphere
    ) {
        return (Planet) CreateBody(new SolarisExtensions.TriPair<>(null, system, null), name, dimension, orbit, distance, shift, size, Planet.class, icon, provider, atmosphere);
    }

    static Star CreateStar(
            SolarSystem system,
            String name, int dimension,
            float orbit, float distance,
            float shift, float size,
            ResourceLocation icon,
            Class<? extends WorldProvider> provider,
            IAtmosphericGas... atmosphere
    ) {
        return (Star) CreateBody(new SolarisExtensions.TriPair<>(system, null, null), name, dimension, orbit, distance, shift, size, Star.class, icon, provider, atmosphere);
    }

    static CelestialBody CreateBody(
            SolarisExtensions.TriPair<SolarSystem, SolarSystem, Planet> owner,
            String name, int dimension,
            float orbit, float distance,
            float shift, float size,
            Class<? extends CelestialBody> clazz,
            ResourceLocation icon,
            Class<? extends WorldProvider> provider,
            IAtmosphericGas... atmosphere
    ) {
        if (CACHE.containsKey(name)) return CACHE.get(name);
        try {
            Constructor<? extends CelestialBody> constructor = clazz.getConstructor(String.class);
            CelestialBody body = constructor.newInstance(name);
            if (owner.left() != null) { // it's a star
                body = ((Star) body).setParentSolarSystem(owner.left());
            } else if (owner.middle() != null) { // planet
                body = ((Planet) body).setParentSolarSystem(owner.middle());
            } else if (owner.right() != null) { // moon
                body = ((Moon) body).setParentPlanet(owner.right());
            }

            body = body.setBodyIcon(icon)
                    .setPhaseShift(shift)
                    .setRelativeSize(size)
                    .setRelativeOrbitTime(orbit)
                    .setDimensionInfo(dimension, provider)
                    .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(distance, distance));

            for (IAtmosphericGas gas : atmosphere) {
                body = body.atmosphereComponent(gas);
            }

            CACHE.put(name, body);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            LoggingHelper.oopsie(Solaris.LOGGER, "FAILED CREATING CELESTIAL BODY '" + name + "'!", exception);
            return null;
        }

        return CACHE.get(name);
    }

    static int CalculateAdvancedTier(int target) {
        int max = 3;
        if (Loader.isModLoaded("ExtraPlanets") || Loader.isModLoaded("MorePlanet")) max = 10;
        if (Loader.isModLoaded("GalaxySpace")) max = 6;

        if (target > max) {
            if (max == 3) Solaris.LOGGER.warn("Invalid rocket tier {}, no supported addon available - falling back to 3; try installing More Planets, Extra Planets, or Galaxy Space.", target);
            if (max == 6) Solaris.LOGGER.warn("Invalid rocket tier {}, neither Extra Planets nor More Planets are available - falling back to GalaxySpace max of 6.", target);
            if (max == 10) Solaris.LOGGER.warn("Invalid rocket tier {}. Do any mods even exist that provide this high a tier...?", target);
        }

        return Math.max(target, max);
    }

    static ResourceLocation GCBodyIcon(String icon) {
        return new ResourceLocation("galacticraftcore", "textures/gui/celestialbodies/" + icon + ".png");
    }

    @Override default float getGravity() { return 0.0F; }
    @Override default float getWindLevel() { return 1.0F; }
    @Override default float getSolarSize() { return 1.0F; }
    @Override default double getMeteorFrequency() { return 0.0; }
    @Override default float getFallDamageModifier() { return 1.0F; }
    @Override default double getFuelUsageMultiplier() { return 1.0; }
    @Override default float getThermalLevelModifier() { return 0.0F; }
    @Override default double getSolarEnergyMultiplier() { return 1.0; }
    @Override default double getYCoordinateToTeleport() {return 800.0; }
    @Override default boolean netherPortalsOperational() { return false; }
    default ITeleportType entryMethod() { return new TeleportTypeLander(); }
    @Override default float getSoundVolReductionAmount() { return this.hasBreathableAtmosphere() ? 0.0F : 10.0F; }
    @Override default boolean isGasPresent(IAtmosphericGas gas) { return this.getCelestialBody().atmosphere.contains(gas); }
    @Override default boolean canSpaceshipTierPass(int tier) { return tier >= this.getCelestialBody().getTierRequirement(); }
    @Override default boolean hasBreathableAtmosphere() { return this.getCelestialBody().atmosphere.contains(IAtmosphericGas.OXYGEN); }
    default ResourceLocation rocketLaunchVisual() { return new ResourceLocation("galacticrafttcore", "textures/gui/overworldRocketGui.png"); }

    @SuppressWarnings("unchecked")
    default void register(int tier) {
        CelestialBody body = this.getCelestialBody();
        body.setTierRequired(CalculateAdvancedTier(tier));
        switch (body.getClass().getSimpleName()) { // this fucking sucks actually
            case "Star" -> StarRegistry.STARS.add((Star) body);
            case "Moon" -> GalaxyRegistry.registerMoon((Moon) body);
            case "Planet" -> GalaxyRegistry.registerPlanet((Planet) body);
        }

        GalacticraftRegistry.registerTeleportType((Class<? extends WorldProvider>) this.getClass(), this.entryMethod());
        GalacticraftRegistry.registerTeleportType((Class<? extends WorldProvider>) this.getClass().getSuperclass(), this.entryMethod());
        GalacticraftRegistry.registerRocketGui((Class<? extends WorldProvider>) this.getClass(), this.rocketLaunchVisual());
    }
}
