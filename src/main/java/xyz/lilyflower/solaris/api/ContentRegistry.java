package xyz.lilyflower.solaris.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import xyz.lilyflower.solaris.content.SolarisRegistryLoader;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.util.SolarisExtensions;

@SuppressWarnings("unused")
public interface ContentRegistry<T> {
    @NotNull // it's only null if it throws
    @SuppressWarnings("SameParameterValue")
    static <T> T create(String name, Class<? extends T> clazz, Class<?>[] types, ArrayList<SolarisExtensions.Pair<T, String>> contents, Object... arguments) {
        try {
            Constructor<? extends T> constructor = clazz.getConstructor(types);
            T instance = constructor.newInstance(arguments);
            contents.add(new SolarisExtensions.Pair<>(instance, name));
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            LoggingHelper.oopsie(SolarisRegistryLoader.LOGGER, "FAILED INITIALIZING OBJECT CLASS: " + clazz.getName(), exception);
            return null;
        }
    }

    ArrayList<SolarisExtensions.Pair<T, String>> contents();
    void register(SolarisExtensions.Pair<T, String> pair);
    boolean valid(String key);
    boolean runnable();
}