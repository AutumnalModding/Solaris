package xyz.lilyflower.solaris.integration.galacticraft;

import cpw.mods.fml.common.Loader;
import java.util.Random;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.botania.common.block.ModBlocks;

public class TeleportTypeLander implements ITeleportType, IExitHeight {
    @Override public boolean useParachute() { return false; }
    @Override public Vector3 getParaChestSpawnLocation(WorldServer server, EntityPlayerMP player, Random random) { return null; }

    public EntityLanderBase lander(EntityPlayerMP occupant) {
        return new EntityLander(occupant);
    }

    @Override
    public double getYCoordinateToTeleport() {
        return 900.0D;
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer server, EntityPlayerMP player) {
        if (player == null) return null;
        GCPlayerStats stats = GCPlayerStats.get(player);

        Block target = Loader.isModLoaded("Botania") ? ModBlocks.bifrostPerm : Blocks.bedrock;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                server.setBlock((int) stats.coordsTeleportedFromX - 2 + x, 0, (int) stats.coordsTeleportedFromZ - 2 + z, target);
            }
        }

        return new Vector3(stats.coordsTeleportedFromX, this.getYCoordinateToTeleport(), stats.coordsTeleportedFromZ);
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer server, Entity entity) {
        return entity == null ? null : new Vector3(entity.posX, this.getYCoordinateToTeleport(), entity.posZ);
    }

    @Override
    public void onSpaceDimensionChanged(World world, EntityPlayerMP player, boolean auto) {
        if (auto || player == null) return;
        GCPlayerStats stats = GCPlayerStats.get(player);
        if (stats.teleportCooldown > 0) return;
        player.capabilities.isFlying = false;
        stats.teleportCooldown = 10;
        if (world.isRemote) return;
        EntityLanderBase lander = lander(player);
        world.spawnEntityInWorld(lander);
    }

    @Override
    public void setupAdventureSpawn(EntityPlayerMP player) {}
}
