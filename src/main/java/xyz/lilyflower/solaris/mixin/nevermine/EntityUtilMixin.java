package xyz.lilyflower.solaris.mixin.nevermine;

import net.nevermine.assist.EntityUtil;
import net.nevermine.mob.placement.EntityHunter;
import net.nevermine.mob.placement.EntityNoRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;
import xyz.lilyflower.solaris.init.Solaris;

@Mixin(value = EntityUtil.class, remap = false)
public class EntityUtilMixin {
    @Redirect(method = "shootEntity", at = @At(value = "CONSTANT", args = "classValue=net/nevermine/mob/placement/EntityNoRange"), require = 1)
    private static boolean allowRange(Object instance, Class<?> type) {
        Solaris.LOGGER.info("Cancelling 1");
        return !NevermineTransformerSettings.ALLOW_ALL && instance instanceof EntityNoRange;
    }

    @Redirect(method = "shootEntity", at = @At(value = "CONSTANT", args = "classValue=net/nevermine/mob/placement/EntityHunter", ordinal = 0), require = 1)
    private static boolean allowGeneral(Object instance, Class<?> type) {
        return !NevermineTransformerSettings.DISABLE_HUNTER_REQUIREMENTS && instance instanceof EntityHunter;
    }
}
