package xyz.lilyflower.solaris.integration.galacticraft.lander;

import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod;
import net.minecraft.entity.player.EntityPlayerMP;

public class TeleportTypeDropPod extends TeleportTypeLander {
    @Override
    public EntityLanderBase lander(EntityPlayerMP occupant) {
        return new EntityEntryPod(occupant);
    }
}
