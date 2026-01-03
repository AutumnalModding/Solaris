package xyz.lilyflower.solaris.mixin.vics;

import com.vicmatskiv.weaponlib.melee.MeleeRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.configuration.modules.SolarisMW;

@Mixin(MeleeRenderer.class)
public class MeleeRendererMixin {
    @Redirect(method = "getStateDescriptor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSprinting()Z", remap = true), remap = false)
    public boolean sprinting(EntityPlayer instance) {
        return instance.isSprinting() && !SolarisMW.ALLOW_SHOOTING_WHEN_SPRINTING;
    }
}
