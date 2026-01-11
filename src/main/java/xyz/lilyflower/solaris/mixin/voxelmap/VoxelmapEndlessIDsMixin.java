package xyz.lilyflower.solaris.mixin.voxelmap;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;
import com.thevoxelbox.voxelmap.a;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = a.class, remap = false)
public class VoxelmapEndlessIDsMixin {
    @Unique private Minecraft solaris$minecraft;
    
    @Inject(method = "int(II)V", cancellable = true, at = @At("HEAD"))
    public void weHaveOverwriteAtHome(int block, int meta, CallbackInfo info) {
        this.solaris$overwriteAtHome(block, meta);
        info.cancel();
    }

    @Inject(method = "do(IIB)I", cancellable = true, at = @At("HEAD"))
    public void weHaveOverwriteAtHome(int block, int meta, byte biome, CallbackInfoReturnable<Integer> info) {
        int value = this.solaris$overwriteAtHome(block, meta, biome);
        info.setReturnValue(value);
    }

    @Unique
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void solaris$overwriteAtHome(int block, int meta) {
        if (this.solaris$minecraft == null) this.solaris$minecraft = Minecraft.getMinecraft();
        WorldClient world = this.solaris$minecraft.theWorld;
        if (world != null) {
            Block target = Block.getBlockById(block);
            if (target != null) {
                int length = BiomeGenBase.getBiomeGenArray().length;
                Integer[] array = new Integer[length];
                Arrays.fill(array, -1);
                int targetX = (int) this.solaris$minecraft.thePlayer.posX - 32;
                int targetZ = (int) this.solaris$minecraft.thePlayer.posZ - 32;
                Chunk chunk = world.getChunkFromBlockCoords(targetX, targetZ);
                ChunkBiomeHook hook = (ChunkBiomeHook) chunk;
                short[] biomes = hook.getBiomeShortArray();
                Block there = world.getBlock(targetX, 0, targetZ);
                int that = world.getBlockMetadata(targetX, 0, targetZ);
                chunk.func_150807_a(targetX & 15, 0, targetZ & 15, target, meta);
                short[] filled = new short[biomes.length];

                for (int biome = 0; biome < length; ++biome) {
                    if (BiomeGenBase.getBiome(biome) != null) {
                        Arrays.fill(filled, (short) biome);
                        hook.setBiomeShortArray(filled);
                        array[biome] = target.colorMultiplier(world, targetX, 0, targetZ) | -16777216;
                    }
                }

                hook.setBiomeShortArray(biomes);
                chunk.func_150807_a(targetX & 15, 0, targetZ & 15, there, that);
                int shifted = block + (meta << 12);
                try {
                    Class clazz = this.getClass();
                    Field field = clazz.getDeclaredField("break");
                    field.setAccessible(true);
                    HashMap<Integer, Integer[]> map = (HashMap<Integer, Integer[]>) field.get(this);
                    map.put(shifted, array);
                    field.set(this, map);
                } catch (ReflectiveOperationException ignored) {}
            }
        }
    }

    @Unique
    @SuppressWarnings("unused")
    public int solaris$overwriteAtHome(int block, int meta, byte biome) {
        if (this.solaris$minecraft == null) this.solaris$minecraft = Minecraft.getMinecraft();
        WorldClient world = this.solaris$minecraft.theWorld;
        Block target = Block.getBlockById(block);
        if (world == null || target == null) return -1;
        int chunkX = (int) (this.solaris$minecraft.thePlayer.posX - 32);
        int chunkZ = (int) (this.solaris$minecraft.thePlayer.posZ - 32);
        Chunk chunk = world.getChunkFromBlockCoords(chunkX, chunkZ);
        ChunkBiomeHook hook = (ChunkBiomeHook) chunk;
        short[] filled = hook.getBiomeShortArray();
        Block there = world.getBlock(chunkX, 0, chunkZ);
        int that = world.getBlockMetadata(chunkX, 0, chunkZ);
        chunk.func_150807_a(chunkX & 15, 0, chunkZ & 15, target, meta);
        int colour = target.colorMultiplier(world, chunkX, 0, chunkZ) | -16777216;
        hook.setBiomeShortArray(filled);
        chunk.func_150807_a(chunkX & 15, 0, chunkZ & 15, there, that);
        return colour;
    }
}
