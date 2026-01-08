package xyz.lilyflower.solaris.integration.galacticraft;

import com.github.bsideup.jabel.Desugar;
import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import net.minecraft.world.WorldProvider;
import xyz.lilyflower.solaris.integration.galacticraft.lander.TeleportTypeLander;
import xyz.lilyflower.solaris.util.data.Colour;

import java.util.Set;
import xyz.lilyflower.solaris.util.data.TypedParam;

@Desugar
public record PlanetData(
        Class<? extends WorldProvider> provider,
        String parent,
        String name,
        String icon,
        String gui,
        Colour rings,
        int dimension,
        int tier,
        float distance,
        float time,
        float size,
        float shift,
        double height,
        double solar,
        double fuel,
        float gravity,
        float meteors,
        float falldamage,
        float sound,
        boolean breathable,
        boolean portals,
        float thermal,
        float wind,
        float closeness,
        Class<? extends CelestialBody> type,
        Class<? extends TeleportTypeLander> lander,
        List<TypedParam> entry,
        Set<IAtmosphericGas> atmosphere
) {}
