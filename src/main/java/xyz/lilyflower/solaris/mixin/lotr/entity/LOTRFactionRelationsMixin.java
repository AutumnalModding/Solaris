package xyz.lilyflower.solaris.mixin.lotr.entity;

import lotr.common.fac.LOTRFaction;
import lotr.common.fac.LOTRFactionRelations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.integration.lotr.LOTRFactionRelationLoader;

@Mixin(LOTRFactionRelations.class)
public class LOTRFactionRelationsMixin {
    @Inject(method = "getRelations", at = @At("HEAD"), cancellable = true, remap = false)
    private static void override(LOTRFaction f1, LOTRFaction f2, CallbackInfoReturnable<LOTRFactionRelations.Relation> cir) {
        LOTRFactionRelations.FactionPair pair = new LOTRFactionRelations.FactionPair(f1, f2);
        if (LOTRFactionRelationLoader.RELATIONS.containsKey(pair)) {
            cir.setReturnValue(LOTRFactionRelationLoader.RELATIONS.get(pair));
        }
    }
}