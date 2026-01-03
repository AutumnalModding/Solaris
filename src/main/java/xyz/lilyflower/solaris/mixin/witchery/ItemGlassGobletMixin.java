package xyz.lilyflower.solaris.mixin.witchery;

import com.emoniph.witchery.item.ItemGlassGoblet;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.configuration.modules.SolarisWitchery;

@Mixin(ItemGlassGoblet.class)
public class ItemGlassGobletMixin {
    @Redirect(method = "onItemRightClick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldProvider;dimensionId:I", opcode = Opcodes.GETFIELD))
    public int allowOtherDimensions(WorldProvider instance) {
        return SolarisWitchery.ALLOWED_RITUAL_DIMENSIONS.contains(instance.dimensionId) ? 0 : Integer.MAX_VALUE;
    }
}