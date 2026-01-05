package xyz.lilyflower.solaris.integration.galacticraft.lander;

import galaxyspace.core.prefab.entity.EntityEntryPod;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.init.Solaris;

public class TeleportTypeRandom extends TeleportTypeLander {
    private final int maxX;
    private final int maxZ;
    private final Class<? extends EntityLanderBase> lander;

    public TeleportTypeRandom(int x, int y, Class<? extends EntityLanderBase> lander) {
        this.maxX = x;
        this.maxZ = y;
        this.lander = lander;
    }

    @Override
    public EntityLanderBase lander(EntityPlayerMP occupant) {
        try {
            Constructor<? extends EntityLanderBase> constructor = lander.getConstructor(EntityPlayerMP.class);
            return constructor.newInstance(occupant);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            LoggingHelper.oopsie(Solaris.LOGGER, "FAILED INSTANTIATING LANDER CLASS: " + lander.getCanonicalName(), exception);
            return new EntityEntryPod(occupant);
        }
    }

    @Override
    public Vector3 getPlayerSpawnLocation(WorldServer server, EntityPlayerMP player) {
        return this.getEntitySpawnLocation(server, player);
    }

    @Override
    public Vector3 getEntitySpawnLocation(WorldServer server, Entity entity) {
        Random random = new Random();
        return entity == null ? null : new Vector3(
                random.nextInt(maxX * 2 + 1) - maxX,
                this.getYCoordinateToTeleport(),
                this.maxZ + random.nextInt(maxZ * 2 + 1) - maxZ
        );
    }
}
