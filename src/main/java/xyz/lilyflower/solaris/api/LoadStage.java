package xyz.lilyflower.solaris.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.init.Solaris;

@SuppressWarnings("unused")
public enum LoadStage {
    BOOTSTRAP(null), // Construction
    PRELOADER(null), // Pre-Init
    RUNNING(null), // Initialization
    FINALIZE(null), // Post-Init
    SPINUP(null) // Server Starting

    ;

    // TODO: whatever the fuck this was?
    LoadStage(@SuppressWarnings("SameParameterValue") Class<? extends LoadHelper> clazz) {
        if (clazz == null) return;
        try {
            Constructor<? extends LoadHelper> constructor = clazz.getConstructor();
            LoadHelper instance = constructor.newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
            LoggingHelper.oopsie(Solaris.LOGGER, "FAILED REGISTERING LOADING HELPER: " + clazz.getName(), exception);
        }
    }

    public interface LoadHelper {

    }
}
