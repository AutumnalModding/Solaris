package xyz.lilyflower.solaris.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ExactCoordinatesTeleporter extends Teleporter {
    private final double x;
    private final double y;
    private final double z;

    public ExactCoordinatesTeleporter(WorldServer world, double x, double y, double z) {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float unknown) {
        entity.setPosition(this.x, this.y, this.z);
    }
}
