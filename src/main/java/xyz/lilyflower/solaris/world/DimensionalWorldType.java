package xyz.lilyflower.solaris.world;

import java.util.HashMap;
import net.minecraft.world.WorldType;

public class DimensionalWorldType extends WorldType {
    public final int dimension;
    public static final HashMap<Integer, DimensionalWorldType> TYPES = new HashMap<>();

    public static void create(int dimension) {
        TYPES.put(dimension, new DimensionalWorldType(dimension));
    }

    private DimensionalWorldType(int dimension) {
        super("dimensional$" + dimension);
        this.dimension = dimension;
    }

    @Override
    public boolean showWorldInfoNotice() {
        return false;
    }
}
