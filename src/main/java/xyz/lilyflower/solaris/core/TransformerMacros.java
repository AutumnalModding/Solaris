package xyz.lilyflower.solaris.core;

import cpw.mods.fml.common.FMLCommonHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.debug.LoggingHelper;
import xyz.lilyflower.solaris.util.reflect.SolarisReflection;

public class TransformerMacros {
    @SuppressWarnings("unused")
    public static void LogMessage(InsnList list, String level, String message) {
        InsnList instructions = new InsnList();

        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "xyz/lilyflower/solaris/core/SolarisBootstrap", "LOGGER", "Lorg/apache/logging/log4j/Logger;"));
        instructions.add(new LdcInsnNode(message));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", level, "(Ljava/lang/String;)V", true));

        list.add(instructions);
    }

    @SuppressWarnings("unused")
    public static void KillJVM(InsnList list, int code, boolean hard) {
        InsnList instructions = new InsnList();

        instructions.add(new IntInsnNode(Opcodes.SIPUSH, code));
        instructions.add(new LdcInsnNode(hard));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "xyz/lilyflower/solaris/util/TransformerMacros", "__INTERNAL_KILL", "(IZ)V", false));

        list.add(instructions);
    }

    public static void PrepareItemForRegister(InsnList list, LabelNode exit, String target, String original, String owner, boolean raw) {
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, owner, original, "Lnet/minecraft/item/Item;"));
        list.add(new JumpInsnNode(Opcodes.IF_ACMPNE, exit));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new LdcInsnNode((raw ? "" : "$APPLYPREFIX$") + target)); // see GameDataTransformer
    }

    public static void GetStaticField(Class<?> clazz, String name, InsnList list) {
        try {
            String owner = Type.getInternalName(clazz);
            Field field = clazz.getDeclaredField(name);
            String descriptor = Type.getDescriptor(field.getType());
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, owner, name, descriptor));
        } catch (Exception exception) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED GETTING FIELD: " + name, exception);
        }
    }

    public static AbstractInsnNode CheckMethodCall(Class<?> clazz, String name, Class<?>[] arguments, MethodInsnNode node) {
        try {
            String owner = Type.getInternalName(clazz);
            Method method = clazz.getDeclaredMethod(name, arguments);
            String descriptor = Type.getMethodDescriptor(method);

            if (SolarisBootstrap.DEBUG_ENABLED) SolarisBootstrap.LOGGER.debug("Validating {}#{}{} against {}#{}{}", owner, name, descriptor, node.owner, node.name, node.desc);
            return (node.owner.equals(owner) && node.desc.equals(descriptor)) ? node : null;
        } catch (Exception exception) {
            LoggingHelper.oopsie(SolarisBootstrap.LOGGER, "FAILED VERFIYING CALL: " + name, exception);
        }

        return null;
    }

    public static void KillMethodCall(Class<?> clazz, String name, Class<?>[] arguments, InsnList list) {
        list.iterator().forEachRemaining(node -> {
            if (node instanceof MethodInsnNode method) {
                if (CheckMethodCall(clazz, name, arguments, method) == null) return;
                Type descriptor = Type.getMethodType(method.desc);
                for (Type argument : descriptor.getArgumentTypes()) { // no voidtype on the stack!
                    list.insertBefore(method, new InsnNode(Opcodes.SASTORE + argument.getSize()));
                }
                if (method.getOpcode() != Opcodes.INVOKESTATIC) list.insertBefore(method, new InsnNode(Opcodes.POP));
                if (SolarisBootstrap.DEBUG_ENABLED)
                    SolarisBootstrap.LOGGER.debug("Killing call to {}#{}{}", method.owner, method.name, method.desc);
                list.remove(method);
            }
        });
    }

    @SuppressWarnings("unused")
    public static void ReplaceMethodCall(Class<?> clazz, String name, Class<?>[] arguments, InsnList list, MethodInsnNode target) {
        list.iterator().forEachRemaining(node -> {
            if (node instanceof MethodInsnNode method) {
                if (CheckMethodCall(clazz, name, arguments, method) == null) return;
                if (SolarisBootstrap.DEBUG_ENABLED)
                    SolarisBootstrap.LOGGER.debug("Replacing call to {}#{}{} with call to {}#{}{}", method.owner, method.name, method.desc, target.owner, target.name, target.desc);
                list.insertBefore(method, target);
                list.remove(method);
            }
        });
    }

    // TODO: actually implement this
    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void CancelRegistrationForID(InsnList list, int index) {}

    @SuppressWarnings("EmptyMethod")
    public static void __INTERNAL_NOOP() {}

    @SuppressWarnings("unused")
    public static void __INTERNAL_KILL(int code, boolean hard) {
        if (!hard) {
            FMLCommonHandler.instance().exitJava(code, false);
            return;
        }

        // FML is so dumb.
        SolarisReflection.invoke("java.lang.Shutdown", "halt0", code);
    }
}
