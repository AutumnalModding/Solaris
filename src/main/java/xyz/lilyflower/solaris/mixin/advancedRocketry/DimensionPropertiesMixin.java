package xyz.lilyflower.solaris.mixin.advancedRocketry;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.lilyflower.solaris.util.data.AREnhancedPlanetProperties;
import xyz.lilyflower.solaris.util.reflect.SolarisReflection;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

@Mixin(DimensionProperties.class)
public class DimensionPropertiesMixin implements AREnhancedPlanetProperties {
    @Unique
    public IAtmosphere solaris$direct = null;

    @Unique
    public String solaris$defined = "";

    @Override
    public IAtmosphere solaris$direct() {
        return this.solaris$direct;
    }

    @Override
    public String solaris$defined() {
        return this.solaris$defined;
    }

    @Override
    public void solaris$direct(IAtmosphere type) {
        this.solaris$direct = type;
    }

    @Override
    public void solaris$defined(String name) {
        this.solaris$defined = name;
        this.solaris$direct = SolarisReflection.get(AtmosphereType.class, name);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    public void readEnhanced(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("atmosType")) {
            String type = nbt.getString("atmosType").toUpperCase();
            this.solaris$defined(type);
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    public void writeEnhanced(NBTTagCompound nbt, CallbackInfo ci) {
        if (this.solaris$direct != null) {
            nbt.setString("atmosType", this.solaris$defined);
        }
    }

    @Inject(method = "getAtmosphere", at = @At("HEAD"), cancellable = true, remap = false)
    public void getDefinedAtmosphere(CallbackInfoReturnable<IAtmosphere> cir) {
        if (this.solaris$direct != null) {
            cir.setReturnValue(this.solaris$direct);
        }
    }
}
