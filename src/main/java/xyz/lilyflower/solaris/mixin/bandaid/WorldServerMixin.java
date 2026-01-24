package xyz.lilyflower.solaris.mixin.bandaid;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Mixin(WorldServer.class) // not in decomp fsr?
@SuppressWarnings("JavaReflectionMemberAccess")
public class WorldServerMixin {
    @Shadow private IntHashMap entityIdMap;

    @SuppressWarnings("rawtypes")//generics aren't real and cannot hurt you
    @Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
    public void fixNulls(Entity entity, CallbackInfo info) {
        if (entity == null) info.cancel();
        if (this.entityIdMap == null) this.entityIdMap = new IntHashMap();

        // IDEA only warns when in the try block? weird.
        WorldServer world = (WorldServer) (Object) this;
        try {
            Class<WorldServer> clazz = WorldServer.class;
            Field field = clazz.getDeclaredField("entitiesByUuid");
            field.setAccessible(true);
            Map map = (Map) field.get(world);
            if (map == null) {
                map = new HashMap();
                field.set(world, map);
            }
        } catch (ReflectiveOperationException ignored) {}
    }

    /**
     * @author Lilyflower
     * @reason fix IAE
     */
    @Overwrite
    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public BiomeGenBase.SpawnListEntry spawnRandomCreature(EnumCreatureType type, int x, int y, int z) {
        WorldServer server = (WorldServer) (Object) this; // TODO: does this REALLY have to be an @Overwrite?
        List<BiomeGenBase.SpawnListEntry> list = (List<BiomeGenBase.SpawnListEntry>) server.getChunkProvider().getPossibleCreatures(type, x, y, z);
        list = ForgeEventFactory.getPotentialSpawns(server, type, x, y, z, list);
        try { return list != null && !list.isEmpty() ? (BiomeGenBase.SpawnListEntry) WeightedRandom.getRandomItem(server.rand, list) : null; }
        catch (IllegalArgumentException exception) { return null; }
    }
}
