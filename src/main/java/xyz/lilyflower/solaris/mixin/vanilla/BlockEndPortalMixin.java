package xyz.lilyflower.solaris.mixin.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.lilyflower.solaris.configuration.modules.SolarisVanilla;

@Mixin(BlockEndPortal.class)
public class BlockEndPortalMixin {
    @ModifyArg(method = "onEntityCollidedWithBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;travelToDimension(I)V"))
    public int retarget(int original, @Local(argsOnly = true) Entity entity) {
        return entity.dimension == 1 ? SolarisVanilla.END_PORTAL_TARGET : 1;
    }
}
