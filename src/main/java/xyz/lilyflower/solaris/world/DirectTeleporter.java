package xyz.lilyflower.solaris.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class DirectTeleporter extends Teleporter {
    public DirectTeleporter(WorldServer target) {
        super(target);
    }

    @Override public void placeInPortal(Entity entity, double x, double y, double z, float unknown) {}
}
