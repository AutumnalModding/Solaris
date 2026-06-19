package xyz.lilyflower.solaris.core.transformers.misc;

import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.core.TransformationHelper;

@SuppressWarnings("unused")
public class MixinInfoTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "org/spongepowered/asm/mixin/transformer/MixinInfo";
    }

    void getTargetClass(TargetData data) {
        if (SolarisBootstrap.DEBUG_ENABLED) {
            SolarisBootstrap.LOGGER.info("Cutting Mixin's parachute, expect things to crash and burn!");
            TransformationHelper.kill("org.spongepowered.asm.mixin.transformer.MixinInfo", "handleTargetError", new Class<?>[]{String.class, boolean.class}, data.method().instructions);
        }
    }
}
