package xyz.lilyflower.solaris.mixin.bandaid;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public class WorldServerMixin {
    @Shadow private IntHashMap entityIdMap;

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    public void fixNullMap(Entity entity, CallbackInfo ci) {
        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }
    }

    /**
     * @author Lilyflower
     * @reason fix IAE
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    public BiomeGenBase.SpawnListEntry spawnRandomCreature(EnumCreatureType type, int x, int y, int z) {
        WorldServer server = (WorldServer) (Object) this;
        List<BiomeGenBase.SpawnListEntry> list = (List<BiomeGenBase.SpawnListEntry>) server.getChunkProvider().getPossibleCreatures(type, x, y, z);
        list = ForgeEventFactory.getPotentialSpawns(server, type, x, y, z, list);
        try { return list != null && !list.isEmpty() ? (BiomeGenBase.SpawnListEntry) WeightedRandom.getRandomItem(server.rand, list) : null; }
        catch (IllegalArgumentException exception) { return null; }
    }
}
