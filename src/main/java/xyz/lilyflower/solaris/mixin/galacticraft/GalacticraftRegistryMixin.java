package xyz.lilyflower.solaris.mixin.galacticraft;

import java.util.Map;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GalacticraftRegistry.class, remap = false)
public class GalacticraftRegistryMixin {
    @Final @Shadow private static Map<Class<? extends WorldProvider>, ITeleportType> teleportTypeMap;

    @Inject(method = "getTeleportTypeForDimension", at = @At("HEAD"), cancellable = true)
    private static void fixTeleportTypes(Class<? extends WorldProvider> clazz, CallbackInfoReturnable<ITeleportType> cir) {
        cir.setReturnValue(teleportTypeMap.getOrDefault(clazz, teleportTypeMap.get(WorldProviderSurface.class)));
    }
}
