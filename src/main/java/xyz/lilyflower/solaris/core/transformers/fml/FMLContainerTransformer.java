package xyz.lilyflower.solaris.core.transformers.fml;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.StartupQuery;
import org.apache.logging.log4j.Level;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.StabilityTransformerSettings;
import xyz.lilyflower.solaris.util.TransformerMacros;

@SuppressWarnings("unused") // "The world state is utterly corrupted" my ass, FML.
public class FMLContainerTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "cpw/mods/fml/common/FMLContainer";
    }

    void readData(TargetData data) {
        if (StabilityTransformerSettings.STABILITY_OVERRIDES) {
            TransformerMacros.KillMethodCall(StartupQuery.class, "abort", new Class<?>[]{}, data.method().instructions);
            TransformerMacros.KillMethodCall(StartupQuery.class, "notify", new Class<?>[]{String.class}, data.method().instructions);
            TransformerMacros.KillMethodCall(FMLLog.class, "log", new Class<?>[]{Level.class, Throwable.class, String.class, Object[].class}, data.method().instructions);
        }
    }
}
