package xyz.lilyflower.solaris.core.transformers.misc;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.core.TransformerMacros;

@SuppressWarnings("unused")
public class MixinClassInfoTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "org/spongepowered/asm/mixin/transformer/ClassInfo";
    }

    void forName(TargetData data) {
        SolarisBootstrap.LOGGER.info("Shutting Mixin the fuck up!");
        TransformerMacros.KillMethodCall(ILogger.class, "catching", new Class<?>[]{Level.class, Throwable.class}, data.method().instructions);
    }
}
