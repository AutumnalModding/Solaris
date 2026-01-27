package xyz.lilyflower.solaris.util.reflect;

import java.util.EnumSet;
import lotr.common.LOTRDimension;
import lotr.common.fac.LOTRFaction;
import lotr.common.fac.LOTRMapRegion;
import lotr.common.world.spawning.LOTRInvasions;

@SuppressWarnings("unused")
public class EnumHelperMappings {
    public static final Class<?>[][] LOTR = {
            {LOTRInvasions.class, LOTRFaction.class},
            {LOTRFaction.class, int.class, LOTRDimension.class, LOTRDimension.DimensionRegion.class, LOTRMapRegion.class, EnumSet.class},
    };
}
