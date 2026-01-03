package xyz.lilyflower.solaris.mixin.lotr.travel;

import lotr.common.world.map.LOTRWaypoint;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.configuration.modules.SolarisLOTR;

@Mixin(LOTRWaypoint.class)
public class LOTRWaypointMixin {
    @Inject(method = "hasPlayerUnlocked", at = @At("HEAD"), cancellable = true, remap = false)
    public void modifyUnlockStatus(EntityPlayer entityplayer, CallbackInfoReturnable<Boolean> cir) {
        LOTRWaypoint waypoint = (LOTRWaypoint) (Object) this;

        if (SolarisLOTR.UNLOCK_WAYPOINTS || SolarisLOTR.isWaypointGlobal(waypoint)) {
            cir.setReturnValue(true);
        }

        if (SolarisLOTR.isWaypointDisabled(waypoint)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isCompatibleAlignment", at = @At("HEAD"), cancellable = true, remap = false)
    public void noLocking(EntityPlayer entityplayer, CallbackInfoReturnable<Boolean> cir) {
        if (SolarisLOTR.NO_WAYPOINT_LOCKING) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isHidden", at = @At("HEAD"), cancellable = true, remap = false)
    public void disableWaypoint(CallbackInfoReturnable<Boolean> cir) {
        LOTRWaypoint waypoint = (LOTRWaypoint) (Object) this;
        if (SolarisLOTR.isWaypointDisabled(waypoint)) {
            cir.setReturnValue(true);
        }
    }
}
