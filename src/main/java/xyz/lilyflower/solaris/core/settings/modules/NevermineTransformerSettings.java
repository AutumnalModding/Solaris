package xyz.lilyflower.solaris.core.settings.modules;

import xyz.lilyflower.solaris.api.TransformerSettingsModule;
import xyz.lilyflower.solaris.core.settings.SolarisTransformerSettings;

public class NevermineTransformerSettings implements TransformerSettingsModule {
    public static boolean DISABLE_HUNTER_REQUIREMENTS = true;
    public static boolean ALLOW_ALL = true;
    public static boolean ALLOW_RANGED = true;
    public static boolean ALLOW_FIRE = false;
    public static boolean ALLOW_MELEE = false;
    public static boolean ALLOW_MAGIC = false;
    public static boolean ALLOW_EXPLOSIONS = false;
    public static boolean ALLOW_BOWS = false;

    @Override
    public void init() {
        SolarisTransformerSettings.add("aoa", configuration -> {
            DISABLE_HUNTER_REQUIREMENTS = configuration.getBoolean("disableHunterRequirements", "nevermine.skills", true, "Disables AoA/Nevermine's Hunter skill checks for entities.");
            ALLOW_ALL = configuration.getBoolean("allowAll", "nevermine.damage", true, "Clobbers AoA entities over the head with a wet trout and allows them to take damage normally.");
            ALLOW_RANGED = configuration.getBoolean("allowRanged", "nevermine.damage", true,
            """
            This, and the other five options, forcibly allow AoA entities to take specific damage types. However, the others leave a lot to be desired due to how the patches work.
            They don't actually... check specifics. So if you have an entity that can't take melee OR magic, and -one- of those is enabled, they'll end up taking all types anyway.
            All the patches do is change a bit of code to work the same way, depending on which of these is enabled. However, keep in mind that enabling ranged has actual effects.
            """
            );
            ALLOW_FIRE = configuration.getBoolean("allowFire", "nevermine.damage", false, "");
            ALLOW_MAGIC = configuration.getBoolean("allowMagic", "nevermine.damage", false, "");
            ALLOW_MELEE = configuration.getBoolean("allowMelee", "nevermine.damage", false, "");
            ALLOW_EXPLOSIONS = configuration.getBoolean("allowExplosions", "nevermine.damage", false, "");
            ALLOW_BOWS = configuration.getBoolean("allowBows", "nevermine.damage", false, "");
        });
    }
}
