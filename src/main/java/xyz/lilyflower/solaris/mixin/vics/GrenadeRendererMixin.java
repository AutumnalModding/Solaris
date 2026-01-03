package xyz.lilyflower.solaris.mixin.vics;

import com.vicmatskiv.weaponlib.grenade.GrenadeRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.configuration.modules.SolarisMW;

@Mixin(GrenadeRenderer.class)
public class GrenadeRendererMixin {
    @Redirect(method = "getStateDescriptor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isSprinting()Z", remap = true), remap = false)
    public boolean sprinting(EntityLivingBase instance) {
        return instance.isSprinting() && !SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING;
    }
}
