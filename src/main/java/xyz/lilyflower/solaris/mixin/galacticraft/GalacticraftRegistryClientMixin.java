package xyz.lilyflower.solaris.mixin.galacticraft;

import java.util.Map;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GalacticraftRegistry.class, remap = false)
public class GalacticraftRegistryClientMixin {
    @Final @Shadow private static Map<Class<? extends WorldProvider>, ResourceLocation> rocketGuiMap;

    @Inject(method = "getResouceLocationForDimension", at = @At("HEAD"), cancellable = true)
    private static void fixRocketGui(Class<? extends WorldProvider> clazz, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(rocketGuiMap.getOrDefault(clazz, rocketGuiMap.get(WorldProviderSurface.class)));
    }
}
