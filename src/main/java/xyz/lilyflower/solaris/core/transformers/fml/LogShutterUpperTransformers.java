package xyz.lilyflower.solaris.core.transformers.fml;

import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.util.TransformerMacros;

@SuppressWarnings("unused")
public class LogShutterUpperTransformers {
    public static class JarDiscovererDelogger implements SolarisClassTransformer {
        @Override
        public String internal$transformerTarget() {
            return "cpw/mods/fml/common/discovery/JarDiscoverer";
        }

        void discover(TargetData data) { // Oh my God shut the Fuck Up
            TransformerMacros.KillMethodCall(FMLLog.class, "log", new Class<?>[]{Level.class, Throwable.class, String.class, Object[].class}, data.method().instructions);
        }
    }
}
