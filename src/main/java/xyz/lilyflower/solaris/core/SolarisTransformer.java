package xyz.lilyflower.solaris.core;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.lang.reflect.Constructor;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import java.security.ProtectionDomain;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import java.security.NoSuchAlgorithmException;
import org.objectweb.asm.tree.AbstractInsnNode;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.instrument.ClassFileTransformer;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import java.lang.reflect.InvocationTargetException;
import xyz.lilyflower.solaris.util.ClasspathScanning;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

public class SolarisTransformer implements ClassFileTransformer {
    private static boolean CASCADING = false;
    static LaunchClassLoader LOADER = null;
    public static final boolean DEBUG_ENABLED = Files.exists(Paths.get(".classes/")) || System.getProperty("solaris.debug") != null;
    private static final HashMap<String, Class<? extends SolarisClassTransformer>> TRANSFORMERS = new HashMap<>();

    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        name = reader.getClassName();

        if ((node.access & Opcodes.ACC_INTERFACE) != 0) return bytes;
        if (DEBUG_ENABLED) SolarisBootstrap.LOGGER.debug("Class: {}", name.replaceAll("/", "∕"));

        if (TRANSFORMERS.containsKey(name)) {
            SolarisBootstrap.LOGGER.debug("Transforming {} with transformer {} directly", name, TRANSFORMERS.get(name).getSimpleName());
            bytes = transform(name, bytes);
        }

        if (TRANSFORMERS.containsKey(node.superName)) {
            SolarisBootstrap.LOGGER.debug("Transforming {} via superclass {} with transformer {}", name, node.superName, TRANSFORMERS.get(node.superName).getSimpleName());
            bytes = transform(node.superName, bytes);
        }

        List<String> interfaces = new ArrayList<>(node.interfaces);
        for (String iface : interfaces) {
            if (TRANSFORMERS.containsKey(iface)) {
                SolarisBootstrap.LOGGER.debug("Transforming {} via interface {} with transformer {}", name, iface, TRANSFORMERS.get(iface).getSimpleName());
                bytes = transform(iface, bytes);
            }
        }

        if (DEBUG_ENABLED) {
            File dump = new File(".classes/" + name.replaceAll("/", "∕") + ".class");
            try (FileOutputStream output = new FileOutputStream(dump)) {
                output.write(bytes);
            } catch (IOException exception) {
                LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED DUMPING CLASS: " + name, exception);
            }
        }

        return bytes;
    }

    @SuppressWarnings("deprecation") // 'since java 9' yeah good thing this is java 8 then
    private byte[] transform(String name, byte[] bytes) {
        try {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            reader.accept(node, 0);
            
            boolean modified = false;
            Class<? extends SolarisClassTransformer> transformer = TRANSFORMERS.get(name);
            SolarisClassTransformer instance = transformer.newInstance();
            ArrayList<String> methods = new ArrayList<>();
            Arrays.stream(transformer.getDeclaredMethods()).iterator().forEachRemaining(method -> methods.add(method.getName()));

            if (methods.contains("solaris$metadata")) modified |= invoke(transformer, instance, node, null); // Woe, long ass line upon ye. I don't -need- to do it this way but it's Funny lmao
            for (MethodNode method : node.methods) if (methods.contains(method.name.replaceAll("<", "solaris\\$").replaceAll(">", "")) && (method.access & Opcodes.ACC_ABSTRACT) == 0) modified |= invoke(transformer, instance, node, method);

            if (modified) {
                node.accept(writer);
                bytes = writer.toByteArray();
            }
        } catch (Throwable exception) { // this is bad practice but fuck it
            if (exception instanceof NoClassDefFoundError error && !CASCADING) {
                CASCADING = true;
                String target = error.getMessage().replaceAll("/", ".").replace("java.lang.ClassNotFoundException: ", "");
                bytes = retransform(target, name, bytes);
            } else if (exception instanceof RuntimeException runtime && runtime.getMessage().contains("ClassNotFoundException") && !CASCADING) {
                CASCADING = true;
                String target = runtime.getMessage().replaceAll("/", ".").replace("java.lang.ClassNotFoundException: ", "");
                bytes = retransform(target, name, bytes);
            }
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED TRANSFORMING CLASS: " + name, exception);
        }

        return bytes;
    }

    private byte[] retransform(String target, String name, byte[] bytes) {
        try {
//            SolarisBootstrap.LOGGER.warn("Warning: Attempting to cascade class: {}", target);
            LOADER.loadClass(target);
            CASCADING = false;
//            SolarisBootstrap.LOGGER.warn("WARNING: RETRANSFORMING CLASS {}!", name);
            bytes = transform(name, bytes);
        } catch (ClassNotFoundException ignored) {}

        return bytes;
    }

    static {
        SolarisBootstrap.LOGGER.debug("Scanning class transformers...");
        List<Class<SolarisClassTransformer>> classes = ClasspathScanning.implementations(SolarisClassTransformer.class, false, false);

        for (Class<SolarisClassTransformer> clazz : classes) {
            try {
                Constructor<? extends SolarisClassTransformer> constructor = clazz.getConstructor();
                SolarisClassTransformer transformer = constructor.newInstance();
                String target = transformer.internal$transformerTarget();
                TRANSFORMERS.put(target, clazz);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
                LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED LOADING CLASS TRANSFORMER: " + clazz.getSimpleName(), exception);
            }
        }

        TRANSFORMERS.forEach((target, clazz) -> SolarisBootstrap.LOGGER.debug("Registered class transformer {} targeting {}!", clazz.getSimpleName(), target));
    }

    private static boolean invoke(Class<? extends SolarisClassTransformer> transformer, SolarisClassTransformer instance, ClassNode clazz, @Nullable MethodNode method) {
        try {
            Method patcher = transformer.getDeclaredMethod(method == null ? "solaris$metadata" : method.name, method == null ? ClassNode.class : SolarisClassTransformer.TargetData.class);
            patcher.setAccessible(true);

            Integer hash = null;
            Integer node = null;
            if (method != null) {
                hash = compute(method.instructions);
            } else {
                node = compute(clazz);
            }

            patcher.invoke(instance, method != null ? new SolarisClassTransformer.TargetData(clazz, method) : clazz);
            if (hash != null) {
                return hash != compute(method.instructions);
            } else {
                return node != compute(clazz);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException exception) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED TRANSFORMING METHOD" + (method == null ? " METADATA" : ": " + method.name), exception);
        }

        return false;
    }

    private static int compute(InsnList list) {
        AtomicInteger hash = new AtomicInteger(list.size());
        list.iterator().forEachRemaining(node -> {
            int computed = 31 * hash.get() + node.getOpcode();
            switch (node.getType()) {
                case AbstractInsnNode.METHOD_INSN -> {
                    MethodInsnNode method = (MethodInsnNode) node;
                    try {
                        MessageDigest digest = MessageDigest.getInstance("MD5");
                        digest.update(method.name.getBytes());
                        digest.update(method.desc.getBytes());
                        digest.update(method.owner.getBytes());
                        computed += Arrays.hashCode(digest.digest());
                    } catch (NoSuchAlgorithmException ignored) {}
                }

                case AbstractInsnNode.JUMP_INSN -> {
                    JumpInsnNode jump = (JumpInsnNode) node;
                    computed += jump.label.hashCode();
                }
            }
            hash.set(computed);
        });
        return hash.get();
    }

    private static int compute(ClassNode node) {
        int hash = node.access;
        hash = 31 * hash + node.name.hashCode();
        hash = 31 * hash + (node.superName != null ? node.superName.hashCode() : 0);
        hash = 31 * hash + node.interfaces.hashCode();
        hash = 31 * hash + node.interfaces.size();
        hash = 31 * hash + node.methods.size();
        hash = 31 * hash + node.fields.size();
        return hash;
    }
}
