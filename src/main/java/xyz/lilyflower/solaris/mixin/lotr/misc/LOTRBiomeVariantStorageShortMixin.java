package xyz.lilyflower.solaris.mixin.lotr.misc;

import com.falsepattern.endlessids.mixin.helpers.LOTRBiomeVariantStorageShort;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.core.TransformerMacros;

@SuppressWarnings("UnusedMixin")
@Mixin(value = LOTRBiomeVariantStorageShort.class, priority = 1500, remap = false)
public class LOTRBiomeVariantStorageShortMixin {
    @Redirect(method = "sendChunkVariantsToPlayer", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLLog;severe(Ljava/lang/String;[Ljava/lang/Object;)V"), require = 1)
    private static void ohMyGodShutTheFuckUp(String message, Object[] data) {
        TransformerMacros.__INTERNAL_NOOP();
    }
}
