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

@SuppressWarnings("unused")
public class TeleportTypeRadius extends TeleportTypeLander {
    private final int centerX;
    private final int centerZ;
    private final int radius;
    private final Class<? extends EntityLanderBase> lander;

    public TeleportTypeRadius(int centerX, int centerZ, int radius, Class<? extends EntityLanderBase> lander) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
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
                this.centerX + random.nextInt(radius * 2 + 1) - radius,
                this.getYCoordinateToTeleport(),
                this.centerZ + random.nextInt(radius * 2 + 1) - radius
        );
    }
}
