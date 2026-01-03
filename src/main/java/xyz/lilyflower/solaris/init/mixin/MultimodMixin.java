package xyz.lilyflower.solaris.init.mixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public enum MultimodMixin {
    SoulShot("quiverbow.SoulShotMixin", MixinTarget.QUIVERBOW), // not actually multimod lol, but needs to be here
    LOTRTickHandlerClient("lotr.bug.LOTRTickHandlerClientMixin", Side.CLIENT, MixinTarget.LOTR, MixinTarget.WITCHERY),

    ;

    public final String mixin;
    public final List<MixinTarget> targets;
    private final Side side;

    MultimodMixin(String mixin, Side side, MixinTarget... targets) {
        this.mixin = mixin;
        this.targets = Arrays.asList(targets);
        this.side = side;
    }

    MultimodMixin(String mixin, MixinTarget... targets) {
        this.mixin = mixin;
        this.targets = Arrays.asList(targets);
        this.side = Side.BOTH;
    }

    public boolean shouldLoad(List<MixinTarget> loaded) {
        return (side == Side.BOTH
                || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && new HashSet<>(loaded).containsAll(targets);
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }
}