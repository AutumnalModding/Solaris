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
public class TeleportTypeRandom extends TeleportTypeLander {
    private final int radiusX;
    private final int radiusZ;
    private final Class<? extends EntityLanderBase> lander;

    public TeleportTypeRandom(int radiusX, int radiusZ, Class<? extends EntityLanderBase> lander) {
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
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
                random.nextInt(radiusX * 2 + 1) - radiusX,
                this.getYCoordinateToTeleport(),
                this.radiusZ + random.nextInt(radiusZ * 2 + 1) - radiusZ
        );
    }
}
