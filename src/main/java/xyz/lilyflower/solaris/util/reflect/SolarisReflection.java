package xyz.lilyflower.solaris.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import xyz.lilyflower.solaris.core.SolarisBootstrap;

@SuppressWarnings("unchecked")
public class SolarisReflection {
    public static <T, C> T get(Class<C> clazz, String name, Object instance) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    public static <T, C> T get(Class<C> clazz, String name) {
       return get(clazz, name, null);
    }

    public static <T> T get(String type, String name, Object instance) {
        try {
            Class<?> clazz = Class.forName(type);
            return get(clazz, name, instance);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    public static <T> T get(String type, String name) {
        return get(type, name, null);
    }

    public static <T, C> void set(Class<C> clazz, String name, C instance, T object) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, object);
        } catch (ReflectiveOperationException ignored) {}
    }

    public static <T, C> void set(Class<C> clazz, String name, T object) {
        set(clazz, name, null, object);
    }

    public static <T> void set(String type, String name, Object instance, T object) {
        try {
            Class<?> clazz = Class.forName(type);
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, object);
        } catch (ReflectiveOperationException ignored) {}
    }

    public static <T> void set(String type, String name, T object) {
        set(type, name, null, object);
    }

    public static <C, R, T> R invoke(Class<C> clazz, String name, T instance, Object... arguments) {
        try {
            Method[] possible = clazz.getDeclaredMethods();
            List<Class<?>> parameters = new ArrayList<>();
            for (Object argument : arguments) {
                parameters.add(argument.getClass());
            }

            Optional<Method> target = Arrays.stream(possible)
                    .filter(method -> method.getName().equals(name))
                    .filter(method -> new HashSet<>(parameters).containsAll(Arrays.asList(method.getParameterTypes())))
                    .findFirst();

            if (!target.isPresent()) {
                SolarisBootstrap.LOGGER.warn("No method found with name {} and parameters {}", name, parameters);
                return null;
            }

            Method method = target.get();
            return (R) method.invoke(instance, arguments);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    public static <C, R> R invoke(Class<C> clazz, String name, Object... arguments) {
        return invoke(clazz, name, null, arguments);
    }

    public static <R> R invoke(String type, String name, Object instance, Object... arguments) {
        try {
            Class<?> clazz = Class.forName(type);
            return invoke(clazz, name, instance, arguments);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    public static <R> R invoke(String type, String name, Object[] arguments) {
        return invoke(type, name, null, arguments);
    }
}
