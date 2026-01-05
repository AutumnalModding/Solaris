package xyz.lilyflower.solaris.mixin.galacticraft;

import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;

@Mixin(value = EntityEntryPod.class, remap = false)
public class EntityEntryPodMixin {
    @Inject(method = "getInitialMotionY", at = @At("HEAD"), cancellable = true)
    public void nyoom(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((double) SolarisGalacticraft.ENTRY_POD_SPEED);
    }

    @Inject(method = "shouldSpawnParticles", at = @At("HEAD"), cancellable = true)
    public void zoom(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
