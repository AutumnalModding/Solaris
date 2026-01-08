package xyz.lilyflower.solaris.integration.galacticraft;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import xyz.lilyflower.solaris.util.data.TypedParam;

public abstract class PlanetProvider implements IGalacticraftWorldProvider, IExitHeight, ISolarLevel {
    private final PlanetData data;
    private final CelestialBody body;

    public PlanetProvider(PlanetData data) {
        this.data = data;
        try {
            Constructor<? extends CelestialBody> constructor = data.type().getConstructor(String.class);
            CelestialBody body = constructor.newInstance(data.name())
                    .setTierRequired(data.tier())
                    .setBodyIcon(new ResourceLocation("galacticraftcore", "textures/gui/celestialbodies/" + data.icon() + ".png"))
                    .setDimensionInfo(data.dimension(), data.provider())
                    .setPhaseShift(data.shift())
                    .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(data.distance(), data.distance()))
                    .setRelativeOrbitTime(data.time())
                    .setRelativeSize(data.size())
                    .setRingColorRGB(data.rings().red(), data.rings().green(), data.rings().blue());

            for (IAtmosphericGas gas : data.atmosphere()) {
                body = body.atmosphereComponent(gas);
            }

            switch (data.type().getSimpleName()) {
                case "Planet" -> this.body = ((Planet) body).setParentSolarSystem(GalaxyRegistry.getRegisteredSolarSystems().get(data.parent()));
                case "Moon" -> this.body = ((Moon) body).setParentPlanet(GalaxyRegistry.getRegisteredPlanets().get(data.parent()));
                case "Star" -> this.body = ((Star) body).setParentSolarSystem(GalaxyRegistry.getRegisteredSolarSystems().get(data.parent()));
                default -> this.body = null;
            }

        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void register() {
        switch (this.data.type().getSimpleName()) {
            case "Planet" -> GalaxyRegistry.registerPlanet((Planet) this.body);
            case "Moon" -> GalaxyRegistry.registerMoon((Moon) this.body);
            case "Star" -> {
                PlanetParser.STARS.add((Star) this.body);
                GalaxyRegistry.getRegisteredSolarSystems().get(this.data.parent()).setMainStar((Star) this.body);
            }
        }

        ResourceLocation gui = new ResourceLocation("galacticraftcore", "textures/gui/" + this.data.gui() + "RocketGui.png");
        GalacticraftRegistry.registerRocketGui(this.data.provider(), gui);

        try {
            List<Class<?>> classes = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<TypedParam> arguments = data.entry() == null ? new ArrayList<>() : data.entry();
            for (TypedParam param : arguments) {
                classes.add(param.getType());
                values.add(param.getValue());
            }
            Constructor<? extends ITeleportType> constructor = this.data.lander().getConstructor(classes.toArray(new Class<?>[0]));
            ITeleportType lander = constructor.newInstance(values.toArray(new Object[0]));
            GalacticraftRegistry.registerTeleportType(this.data.provider(), lander);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override public float getWindLevel() { return this.data.wind(); }
    @Override public float getGravity() { return this.data.gravity(); }
    @Override public float getSolarSize() { return this.data.closeness(); }
    @Override public CelestialBody getCelestialBody() { return this.body; }
    @Override public double getMeteorFrequency() { return this.data.meteors(); }
    @Override public double getFuelUsageMultiplier() { return this.data.fuel(); }
    @Override public float getSoundVolReductionAmount() {return this.data.sound(); }
    @Override public float getThermalLevelModifier() { return this.data.thermal(); }
    @Override public double getSolarEnergyMultiplier() { return this.data.solar(); }
    @Override public double getYCoordinateToTeleport() { return this.data.height(); }
    @Override public float getFallDamageModifier() { return this.data.falldamage(); }
    @Override public boolean netherPortalsOperational() { return this.data.portals(); }
    @Override public boolean hasBreathableAtmosphere() { return this.data.breathable(); }
    @Override public boolean canSpaceshipTierPass(int tier) { return tier >= this.data.tier(); }
    @Override public boolean isGasPresent(IAtmosphericGas gas) {return this.data.atmosphere().contains(gas); }
}
