package xyz.lilyflower.solaris.mixin.vics;

import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.WeaponFireAspect;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.solaris.configuration.modules.SolarisMW;

@SuppressWarnings("unused")
@Mixin(WeaponFireAspect.class)
public class WeaponFireAspectMixin {
    @Shadow(remap = false) private static Predicate<PlayerWeaponInstance> sprinting;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void allowSprintFiring(CallbackInfo ci) {
        if (SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING) sprinting = instance -> false;
    }
}
