package xyz.lilyflower.solaris.mixin.galaxyspace;

import galaxyspace.api.BodiesHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;

@Mixin(value = BodiesHelper.class, remap = false)
public class BodiesHelperMixin {
    @Unique private static final Planet solaris$DummyPlanet = new Planet("dummyPlanet");
    @Unique private static final Moon solaris$DummyMoon = new Moon("dummyMoon");
    @Unique private static final SolarSystem solaris$DummySystem = new SolarSystem("dummySystem", "milkyWay");

    @Inject(method = "registerBody", at = @At("HEAD"), cancellable = true)
    private static void nope(CelestialBody body, BodiesHelper.BodiesData bodydata, boolean registr, CallbackInfo ci) {
        if (body == null || body == solaris$DummyPlanet || body == solaris$DummyMoon || !body.getReachable() && SolarisGalacticraft.DISABLE_UNREACHABLE_PLANETS) {
            ci.cancel();
        }
    }

    @Inject(method = "registerMoon", at = @At("HEAD"), cancellable = true)
    private static void nope(Planet parent, String name, String modid, Class<? extends WorldProvider> provider, int dimID, int tier, float phase, float size, float distancefromcenter, float relativetime, CallbackInfoReturnable<Moon> cir) {
        if (parent == null || (provider == null && SolarisGalacticraft.DISABLE_UNREACHABLE_PLANETS) || SolarisGalacticraft.DISABLED_CELESTIAL_BODIES.contains(name)) cir.setReturnValue(solaris$DummyMoon);
    }

    @Inject(method = "registerPlanet", at = @At("HEAD"), cancellable = true)
    private static void nope(SolarSystem system, String name, String modid, Class<? extends WorldProvider> provider, int dimID, int tier, float phase, float size, float distancefromcenter, float relativetime, CallbackInfoReturnable<Planet> cir) {
        if (system == null || (provider == null && SolarisGalacticraft.DISABLE_UNREACHABLE_PLANETS) || SolarisGalacticraft.DISABLED_CELESTIAL_BODIES.contains(name)) cir.setReturnValue(solaris$DummyPlanet);
    }

    @Inject(
            method = "registerSolarSystem(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lmicdoodle8/mods/galacticraft/api/vector/Vector3;Ljava/lang/String;F)Lmicdoodle8/mods/galacticraft/api/galaxies/SolarSystem;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void nope(String modid, String name, String galaxy, Vector3 pos, String starname, float size, CallbackInfoReturnable<SolarSystem> cir) {
        if (SolarisGalacticraft.DISABLED_CELESTIAL_BODIES.contains(name)) cir.setReturnValue(solaris$DummySystem);
    }
}
