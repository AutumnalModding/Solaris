package xyz.lilyflower.solaris.init.mixin;

import java.nio.file.Path;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public enum MixinTarget {
    ALFHEIM("Alfheim", "Alfheim", false),
    WITCHERY("Witchery", "Witchery", true),
    BACKHAND("Backhand", "backhand", true),
    QUIVERBOW("Quiverbow", "QuiverBow", true),
    VICS_MW("Vic's Modern Warfare", "mw_", true),
    VANILLA("Vanilla Minecraft", "unused", true),
    OPENLIGHTS("Openlights", "OpenLights", false),
    ENDLESSIDS("EndlessIDs", "endlessids", false),
    GALACTICRAFT("Galacticraft", "Galacticraft", true),
    LOTR("The Lord of the Rings Mod", "LOTRMod", true),
    RPLE("Right Proper Lighting Engine", "rple", false),
    OPENCOMPUTERS("OpenComputers", "OpenComputers", false),
    ADVANCED_ROCKETRY("Advanced Rocketry", "AdvancedRocketry", true),
    AOA("Advent of Ascension", "Nevermine-Tslat", true),
    NTM("HBM-NTM-", "HBM's Nuclear Tech Mod", true)

    ;

    public final String name;
    public final String prefix;
    public final boolean development;

    MixinTarget(String name, String prefix, boolean development) {
        this.name = name;
        this.prefix = prefix.toLowerCase();
        this.development = development;
    }

    public boolean isMatchingJar(Path path) {
        final String location = path.toString().replaceAll(".*mods/", "");
        final String basename = SolarisExtensions.basename(location).toLowerCase();
        final String extension = SolarisExtensions.extension(location);

        return basename.startsWith(prefix) && "jar".equals(extension);
    }

    @Override
    public String toString() {
        return "MixinTarget{" +
                "modName='" + name + '\'' +
                ", jarNamePrefixLowercase='" + prefix + '\'' +
                '}';
    }
}