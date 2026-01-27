package xyz.lilyflower.solaris.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import xyz.lilyflower.solaris.configuration.modules.SolarisContent;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.data.TriPair;
import xyz.lilyflower.solaris.world.DimensionalWorldType;
import xyz.lilyflower.solaris.world.DirectTeleporter;
import xyz.lilyflower.solaris.world.ExactCoordinatesTeleporter;

public class SolarisPlayerHandler {
    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        World world = player.worldObj;
        WorldProvider provider = world.provider;
        WorldType type = provider.terrainType;
        Solaris.LOGGER.info("Player login detected, in dimension {} with world type {} (provider class {})", provider.dimensionId, type.getWorldTypeName(), provider.getClass().getName());
        if (player instanceof EntityPlayerMP multiplayer) {
            if (type instanceof DimensionalWorldType dimensional && player.dimension == 0) {
                WorldServer there = DimensionManager.getWorld(dimensional.dimension);
                WorldProvider that = there.provider;
                TriPair<Double, Double, Double> location = SolarisContent.COORDINATES.get(dimensional.dimension);
                Teleporter teleporter = location != null ?
                        new ExactCoordinatesTeleporter(there, location.left(), location.middle(), location.right()) :
                        new DirectTeleporter(there);

                Solaris.LOGGER.info("Sending player to dimension {} (provider class {})...", that.dimensionId, that.getClass().getName());
                if (location != null) {
                    Solaris.LOGGER.info("...at coordinates {} {} {}.", location.left(), location.middle(), location.right());
                }
                MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(multiplayer, dimensional.dimension, teleporter);
            }
        }
    }
}
