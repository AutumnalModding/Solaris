package xyz.lilyflower.solaris.mixin.lotr.bug;

import cpw.mods.fml.common.gameevent.TickEvent;
import lotr.client.LOTRClientProxy;
import lotr.client.LOTRTickHandlerClient;
import lotr.client.sound.LOTRMusic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LOTRTickHandlerClient.class)
public class LOTRTickHandlerClientMixinTwo {
    @Inject(method = "onClientTick", at = @At("HEAD"), remap = false)
    public void fixRenderer(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (LOTRClientProxy.musicHandler == null) LOTRClientProxy.musicHandler = new LOTRMusic();
    }
}
