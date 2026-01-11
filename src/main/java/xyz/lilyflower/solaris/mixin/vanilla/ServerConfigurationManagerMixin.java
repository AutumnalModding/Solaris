package xyz.lilyflower.solaris.mixin.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.lilyflower.solaris.core.settings.modules.VanillaTransformerSettings;
import xyz.lilyflower.solaris.init.Solaris;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {
    @ModifyVariable(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTracker;removePlayerFromTrackers(Lnet/minecraft/entity/player/EntityPlayerMP;)V"), argsOnly = true)
    private int modify(final int dimension, @Local World world) {
        Solaris.LOGGER.info("orig: {}", dimension);
        int target = dimension == 0 ? VanillaTransformerSettings.DEFAULT_RESPAWN_DIMENSION : dimension;
        if (target == dimension) {
            target = VanillaTransformerSettings.PROVIDER_RESPAWNS.getOrDefault(world.provider.getClass().getName().replace('.', '/'), dimension);
        }
        return target;
    }
}
