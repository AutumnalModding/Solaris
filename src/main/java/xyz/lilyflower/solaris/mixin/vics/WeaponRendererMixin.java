package xyz.lilyflower.solaris.mixin.vics;

import com.vicmatskiv.weaponlib.WeaponRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.configuration.modules.SolarisMW;

@Mixin(WeaponRenderer.class)
public class WeaponRendererMixin {
    @Redirect(method = "getFirstPersonStateDescriptor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isSprinting()Z", remap = true), remap = false)
    public boolean sprintingFirstPerson(EntityLivingBase instance) {
        return instance.isSprinting() && !SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING;
    }

    @Redirect(method = "getThirdPersonStateDescriptor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isSprinting()Z", remap = true), remap = false)
    public boolean sprintingThirdPerson(EntityLivingBase instance) {
        return instance.isSprinting() && !SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING;
    }
}
