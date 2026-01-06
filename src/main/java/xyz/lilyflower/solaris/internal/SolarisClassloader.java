package xyz.lilyflower.solaris.internal;

import java.util.HashMap;
import xyz.lilyflower.solaris.init.Solaris;

// this is super fuckin cursed but needed for planets
public class SolarisClassloader extends ClassLoader {
    static final HashMap<String, Class<?>> LOADED = new HashMap<>();
    private static final SolarisClassloader INSTANCE = new SolarisClassloader(Solaris.class.getClassLoader());

    private SolarisClassloader(ClassLoader parent) {
        super(parent);
    }

    // use via reflection lmao
    public Class<?> load(String name, byte[] bytes) {
        if (LOADED.containsKey(name)) {
            return LOADED.get(name);
        }

        Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
        this.resolveClass(clazz);
        LOADED.put(name, clazz);
        return clazz;
    }
}
