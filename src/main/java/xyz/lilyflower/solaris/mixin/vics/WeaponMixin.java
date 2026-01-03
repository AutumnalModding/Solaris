package xyz.lilyflower.solaris.mixin.vics;

import com.vicmatskiv.weaponlib.Weapon;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.configuration.modules.SolarisMW;

@Mixin(Weapon.class)
public class WeaponMixin {
    @Redirect(method = "getCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isSprinting()Z", remap = true), remap = false)
    public boolean sprinting(EntityLivingBase instance) {
        return instance.isSprinting() && !SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING;
    }
}
