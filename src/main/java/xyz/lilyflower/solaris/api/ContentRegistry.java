package xyz.lilyflower.solaris.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.annotation.Nonnull;
import xyz.lilyflower.solaris.init.SolarisRegistryLoader;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.util.data.Pair;

@SuppressWarnings("unused")
public interface ContentRegistry<T> {
    Class<?>[] EMPTY = new Class<?>[]{};

    @Nonnull // it's only null if it throws
    @SuppressWarnings("SameParameterValue")
    static <T> T create(String name, Class<? extends T> clazz, Class<?>[] types, List<Pair<T, String>> contents, Object... arguments) {
        try {
            Constructor<? extends T> constructor = clazz.getConstructor(types);
            T instance = constructor.newInstance(arguments);
            contents.add(new Pair<>(instance, name));
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            LoggingHelper.oopsie(SolarisRegistryLoader.LOGGER, "FAILED INITIALIZING OBJECT CLASS: " + clazz.getName(), exception);
            return null;
        }
    }

    List<Pair<T, String>> contents();
    void register(Pair<T, String> pair);
    boolean valid(String key);
    boolean runnable();
}