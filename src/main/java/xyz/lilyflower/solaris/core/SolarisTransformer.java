package xyz.lilyflower.solaris.core;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import java.security.MessageDigest;
import java.lang.reflect.Constructor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import java.security.ProtectionDomain;
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
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import java.lang.reflect.InvocationTargetException;
import xyz.lilyflower.solaris.util.reflect.ClasspathScanning;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.api.SolarisGlobalTransformer;

@SuppressWarnings({"rawtypes", "unchecked"}) // java is dumb sometimes
public class SolarisTransformer implements ClassFileTransformer {
    private static final Map<String, byte[]> CACHE = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger("Solaris Class Transformers");
    private static boolean CASCADING = false;
    static final ClassLoader LOADER = SolarisTransformer.class.getClassLoader();
    private static final Map<String, Class> TRANSFORMERS = new HashMap<>();
    private static final Map<String, Class> SUPERPATCHERS = new HashMap<>();

    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        name = reader.getClassName();

        if ((node.access & Opcodes.ACC_INTERFACE) != 0) return bytes;
        if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Loading: {}", name);

        if (TRANSFORMERS.containsKey(name)) {
            SolarisBootstrap.LOGGER.debug("Transforming {} with transformer {} directly", name, TRANSFORMERS.get(name).getSimpleName());
            bytes = transform(name, TRANSFORMERS, bytes, false);
        }

        if (SUPERPATCHERS.containsKey(node.superName)) {
            SolarisBootstrap.LOGGER.debug("Transforming {} via direct superclass {} with transformer {}", name, node.superName, SUPERPATCHERS.get(node.superName).getSimpleName());
            bytes = transform(node.superName, SUPERPATCHERS, bytes, true);
        }

        List<String> interfaces = new ArrayList<>(node.interfaces);
        for (String iface : interfaces) {
            if (TRANSFORMERS.containsKey(iface)) {
                SolarisBootstrap.LOGGER.debug("Transforming {} via interface {} with transformer {}", name, iface, TRANSFORMERS.get(iface).getSimpleName());
                bytes = transform(iface, TRANSFORMERS, bytes, true);
            }
        }

        if (SolarisBootstrap.DEBUG_ENABLED) {
            File dump = new File(".classes/" + name.replaceAll("/", "∕") + ".class");
            try (FileOutputStream output = new FileOutputStream(dump)) {
                output.write(bytes);
            } catch (IOException exception) {
                LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED DUMPING CLASS: " + name, exception);
            }
        }

        if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.trace("Storing class {} in cache.", name);
        CACHE.put(name, bytes);
        return bytes;
    }

    @SuppressWarnings({"deprecation"})
    private byte[] transform(String name, Map<String, Class> transformers, byte[] bytes, boolean overrides) {
        try {
            boolean modified = false;
            Class transformer = transformers.get(name);
            Object instance = transformer.newInstance();
            ArrayList<String> methods = new ArrayList<>();
            Arrays.stream(transformer.getDeclaredMethods()).iterator().forEachRemaining(method -> methods.add(method.getName()));

            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(bytes);
            reader.accept(node, 0);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            Set<String> existing = new HashSet<>();
            for (MethodNode method : node.methods) {
                existing.add(method.name + method.desc);
            }

            if (methods.contains("solaris$metadata")) modified |= invoke(transformer, instance, node, null, "solaris$metadata");

            if (overrides) {
                modified |= superpatch(node, transformer, instance, methods, existing);
            }

            for (MethodNode method : node.methods) {
                String target = sanitize(method.name);
                if (methods.contains(target) && (method.access & Opcodes.ACC_ABSTRACT) == 0) {
                    if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Found method {}{}", method.name, method.desc);
                    modified |= invoke(transformer, instance, node, method, target);
                }
            }

            if (modified) {
                if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Modified class {}", name);
                node.accept(writer);
                bytes = writer.toByteArray();
            }
        } catch (Throwable exception) { // this is bad practice but fuck it
            if (!CASCADING) {
                CASCADING = true;
                String target = exception.getMessage().replaceAll("/", ".");
                if (exception instanceof NoClassDefFoundError) {
                    bytes = retransform(target, name, transformers, bytes, overrides);
                } else if (exception instanceof RuntimeException runtime && runtime.getMessage().contains("ClassNotFoundException")) {
                    target = runtime.getMessage().replaceAll("/", ".").replace("java.lang.ClassNotFoundException: ", "");
                    bytes = retransform(target, name, transformers, bytes, overrides);
                } else if (exception instanceof ClassNotFoundException) {
                    bytes = retransform(target, name, transformers, bytes, overrides);
                }
            } else {
                LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED TRANSFORMING CLASS: " + name, exception);
            }
        }

        return bytes;
    }

    private String sanitize(String name) {
        String target = switch(name) {
            case "for", "int", "do", "void", "float", "double",
                 "switch", "case", "default", "if", "boolean", "else",
                 "interface", "class", "enum", "while", "true", "false",
                 "public", "private", "protected", "try", "catch", "finally",
                 "long", "byte", "short", "char", "abstract", "import",
                 "package", "static", "null", "new", "throw", "return",
                 "break", "continue", "throws", "extends", "implements",
                 "super", "this", "final", "native", "strictfp",
                 "synchronized", "transient", "volatile", "instanceof",
                 "assert", "goto", "const", "var", "yield", "record",
                 "sealed", "permits" -> "__" + name;
            default -> name;
        };

        target = target
                .replaceAll("<", "__")
                .replaceAll(">", "__");

        if (!name.isEmpty() && Character.isDigit(name.charAt(0))) {
            target = "__" + name;
        }

        if (name.isEmpty()) {
            target = "__empty";
        }

        return target;
    }

    private boolean superpatch(ClassNode node, Class transformer, Object instance, List<String> targets, Set<String> existing) {
        boolean modified = false;

        try {
            String current = node.superName;

            while (current != null && !current.equals("java/lang/Object")) {
                ClassNode that = new ClassNode();
                byte[] target = CACHE.get(current);
                ClassReader there = new ClassReader(target);
                there.accept(that, 0);

                for (MethodNode override : that.methods) {
                    String signature = override.name + override.desc;

                    if (existing.contains(signature)) {
                        continue;
                    }

                    if (override.name.equals("<init>") ||
                            override.name.equals("<clinit>") ||
                            (override.access & Opcodes.ACC_PRIVATE) != 0 ||
                            (override.access & Opcodes.ACC_STATIC) != 0 ||
                            (override.access & Opcodes.ACC_FINAL) != 0
                    ) {
                        continue;
                    }

                    String normalized = sanitize(override.name);

                    if (targets.contains(normalized)) {
                        if (SolarisBootstrap.DEBUG_ENABLED) {
                            LOGGER.debug("Adding override for superclass method {}{} from {}",
                                    override.name, override.desc, current);
                        }

                        MethodNode method = new MethodNode(
                                override.access & ~Opcodes.ACC_ABSTRACT,
                                override.name,
                                override.desc,
                                override.signature,
                                override.exceptions.toArray(new String[0])
                        );

                        node.methods.add(method);
                        existing.add(signature);
                    }
                }

                current = that.superName;
            }

        } catch (Exception e) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER,
                    "Failed to process superclass methods for " + node.name, e);
        }

        return modified;
    }

    private byte[] retransform(String target, String name, Map<String, Class> transformers, byte[] bytes, boolean overrides) {
        try {
            LOADER.loadClass(target);
            CASCADING = false;
            bytes = transform(name, transformers, bytes, overrides);
        } catch (ClassNotFoundException ignored) {}

        return bytes;
    }

    static {
        SolarisBootstrap.LOGGER.debug("Scanning class transformers...");
        String verbose = System.getProperty("solaris.verboseClasspathScanning");

        List<Class<SolarisClassTransformer>> transformers = ClasspathScanning.implementations(SolarisClassTransformer.class, false, verbose != null);
        List<Class<SolarisGlobalTransformer>> globals = ClasspathScanning.implementations(SolarisGlobalTransformer.class, false, verbose != null);
        transformers.forEach(SolarisTransformer::parseTransformer);
        globals.forEach(SolarisTransformer::parseTransformer);

        TRANSFORMERS.forEach(SolarisTransformer::logTransformerRegistration);
        SUPERPATCHERS.forEach(SolarisTransformer::logTransformerRegistration);
    }

    // for method references lmao
    private static void logTransformerRegistration(String target, Class clazz) {
        SolarisBootstrap.LOGGER.debug("Registered class transformer {} targeting {}!", clazz.getSimpleName(), target);
    }

    private static void parseTransformer(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            Object transformer = constructor.newInstance();
            Method method = clazz.getDeclaredMethod("internal$transformerTarget");
            method.setAccessible(true);
            String target = (String) method.invoke(transformer);
            if (transformer instanceof SolarisGlobalTransformer) SUPERPATCHERS.put(target, clazz);
            else TRANSFORMERS.put(target, clazz);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED LOADING CLASS TRANSFORMER: " + clazz.getSimpleName(), exception);
        }
    }

    private static boolean invoke(Class transformer, Object instance, ClassNode clazz, @Nullable MethodNode method, String target) {
        try {
            Method patcher = transformer.getDeclaredMethod(target, method == null ? ClassNode.class : SolarisClassTransformer.TargetData.class);
            patcher.setAccessible(true);

            Integer hash = null;
            Integer node = null;
            if (method != null) {
                hash = compute(method.instructions);
                if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Modfiying target method {}{}", method.name, method.desc);
            } else {
                node = compute(clazz);
                if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Modfiying class metadata");
            }

            patcher.invoke(instance, method != null ? new SolarisClassTransformer.TargetData(clazz, method) : clazz);
            boolean modified;
            if (hash != null) {
                modified = hash != compute(method.instructions);
                if (modified && SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Target method {}{} modified successfully", method.name, method.desc);
                else if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Did not modify {}{}.", method.name, method.desc);
            } else {
                modified = node != compute(clazz);
                if (modified && SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Class metadata modified successfully");
                else if (SolarisBootstrap.DEBUG_ENABLED) LOGGER.debug("Did not modify class metadata.");
            }
            return modified;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException exception) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED TRANSFORMING " + (method == null ? "CLASS METADATA" : "METHOD: " + method.name), exception);
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

                case AbstractInsnNode.VAR_INSN -> {
                    VarInsnNode var = (VarInsnNode) node;
                    computed += var.var;
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
