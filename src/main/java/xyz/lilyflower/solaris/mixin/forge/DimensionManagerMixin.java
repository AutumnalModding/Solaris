package xyz.lilyflower.solaris.mixin.forge;

import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.util.TransformerMacros;

@Mixin(value = DimensionManager.class, remap = false)
public class DimensionManagerMixin {
    @Redirect(method = "unloadWorlds", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLLog;warning(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static void shutup(String message, Object[] data) {
        TransformerMacros.__INTERNAL_NOOP();
    }
}
