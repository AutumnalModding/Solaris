package xyz.lilyflower.solaris.mixin.forge;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.solaris.configuration.modules.SolarisVanilla;
import xyz.lilyflower.solaris.util.TransformerMacros;

@Mixin(value = DimensionManager.class, remap = false)
public class DimensionManagerMixin {
    @Redirect(method = "unloadWorlds", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLLog;warning(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static void shutup(String message, Object[] data) {
        TransformerMacros.__INTERNAL_NOOP();
    }

    @Inject(method = "setWorld", at = @At("HEAD"), cancellable = true)
    private static void dont(int dimension, WorldServer world, CallbackInfo info) {
        if (SolarisVanilla.NEVER_UNLOAD.contains(dimension) && world == null) info.cancel();
    }
}
