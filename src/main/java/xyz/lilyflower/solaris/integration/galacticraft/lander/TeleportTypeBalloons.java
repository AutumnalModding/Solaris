package xyz.lilyflower.solaris.integration.galacticraft.lander;

import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;
import net.minecraft.entity.player.EntityPlayerMP;

public class TeleportTypeBalloons extends TeleportTypeLander {
    @Override
    public EntityLanderBase lander(EntityPlayerMP occupant) {
        return new EntityLandingBalloons(occupant);
    }

    @Override
    public double getYCoordinateToTeleport() {
        return 1200.0D;
    }
}
