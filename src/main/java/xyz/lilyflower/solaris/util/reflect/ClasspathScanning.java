package xyz.lilyflower.solaris.util.reflect;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClasspathScanning {
    private static final HashMap<Class<?>, List<Class<?>>> CACHE = new HashMap<>();
    public static final Logger LOGGER = LogManager.getLogger("Solaris Classpath Scanner");

    public static <T> List<Class<T>> implementations(Class<T> clazz, boolean concrete, boolean verbose) {
        LOGGER.info("Scanning classpath for {}implementations of {}...", (concrete ? "concrete " : ""), clazz.getCanonicalName());
        if (!CACHE.containsKey(clazz)) {
            ScanResult result = new ClassGraph()
                .enableClassInfo()
                .rejectPackages("java.*")
                .rejectPackages("javax.*")
                .rejectPackages("sun.*")
                .rejectPackages("com.sun.*")
                .rejectPackages("com.azul.*")
                .rejectPackages("jdk.*")
                .rejectPackages("org.spongepowered.*")
                .rejectPackages("org.objectweb.*")
                .rejectPackages("org.lwjgl.*")
                .rejectPackages("org.apache.*")
                .rejectPackages("gnu.trove.*")
                .rejectPackages("com.ibm.*")
                .rejectPackages("io.netty.*")
                .rejectPackages("com.google.*")
                .rejectPackages("scala.*")
                .rejectPackages("kotlin.*")
                .verbose(verbose)
                .scan();

            ClassInfoList classes = concrete ? result.getSubclasses(clazz) : result.getClassesImplementing(clazz);
            List<Class<T>> loaded = new ArrayList<>();
            for (ClassInfo target : classes) {
                LOGGER.info("Found class {}. Loading it.", target.getName());
                loaded.add(target.loadClass(clazz));
            }
            LOGGER.info("Found {} total {}implementations.", classes.size(), (concrete ? "concrete " : ""));
            __STORE(clazz, loaded);
            result.close();
        }

        return __LOAD(clazz);
    }

    private static <T> void __STORE(Class<T> superclass, List<Class<T>> implementations) {
        CACHE.put(superclass, new ArrayList<>(implementations));
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Class<T>> __LOAD(Class<T> superclass) {
        return (List<Class<T>>) (List<?>) CACHE.get(superclass);
    }
}
