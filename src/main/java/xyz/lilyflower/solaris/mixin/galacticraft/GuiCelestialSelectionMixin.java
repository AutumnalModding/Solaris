package xyz.lilyflower.solaris.mixin.galacticraft;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import org.lwjgl.util.vector.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.init.Solaris;

@Mixin(value = GuiCelestialSelection.class, remap = false)
public class GuiCelestialSelectionMixin {
    @Shadow protected int ticksSinceMenuOpen;
    @Shadow @SuppressWarnings("unused") protected Object selectedParent;
    @Unique private final SolarSystem solaris$main = GalaxyRegistry.getRegisteredSolarSystems().get(SolarisGalacticraft.MAIN_SOLAR_SYSTEM);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void setMainSolarSystem(CallbackInfo ci) {
        Solaris.LOGGER.info("Changing Galacticraft selected solar system to {}. If you crash directly after this line, it's invalid!", SolarisGalacticraft.MAIN_SOLAR_SYSTEM);
        this.selectedParent = solaris$main;
    }
    
    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/core/GalacticraftCore;solarSystemSol:Lmicdoodle8/mods/galacticraft/api/galaxies/SolarSystem;", shift = At.Shift.AFTER, opcode = Opcodes.GETSTATIC), cancellable = true)
    public void setMainSolarSystem(int x, int y, int button, CallbackInfo ci) {
        Object old = this.selectedParent;
        this.selectedParent = solaris$main;
        if (old != this.selectedParent) {
            this.ticksSinceMenuOpen = 0;
        }
        ci.cancel();
    }

    @Redirect(method = "getCelestialBodyPosition", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/CelestialBody;getUnlocalizedName()Ljava/lang/String;"))
    public String setMainSolarSystem(CelestialBody instance) {
        return solaris$main.getMainStar() == instance ? "star.sol" : instance.getUnlocalizedName();
    }

    @Inject(method = "getCelestialBodyPosition", at = @At("HEAD"), cancellable = true)
    public void fixNPE(CelestialBody body, CallbackInfoReturnable<Vector3f> cir) {
        if (body == null) {
            cir.setReturnValue(new Vector3f());
        }

        if (body instanceof Planet planet && planet.getParentSolarSystem() == null) {
            Solaris.LOGGER.warn("Planet '{}' has no parent solar system! Returning empty Vector3 to prevent crash.", planet.getName());
            cir.setReturnValue(new Vector3f());
        }
    }
}
