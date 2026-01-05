package xyz.lilyflower.solaris.core.transformers.bug;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

@SuppressWarnings("unused")
public class ChunkAtmosphereHandlerTransformer implements SolarisClassTransformer {
    void tickTerraforming(TargetData data) {
        InsnList list = new InsnList();

        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/hbm/handler/atmosphere/ChunkAtmosphereHandler", "growthMap", "Ljava/util/HashMap;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/World", "field_73011_w", "Lnet/minecraft/world/WorldProvider;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/WorldProvider", "field_76574_g", "I"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayDeque"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayDeque", "<init>", "()V", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "putIfAbsent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
        list.add(new InsnNode(Opcodes.POP));

        data.method().instructions.insert(list);
    }

    void receiveWorldTick(TargetData data) {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "com/hbm/handler/atmosphere/ChunkAtmosphereHandler", "worldBlobs", "Ljava/util/HashMap;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/gameevent/TickEvent$WorldTickEvent", "world", "Lnet/minecraft/world/World;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/World", "field_73011_w", "Lnet/minecraft/world/WorldProvider;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/WorldProvider", "field_76574_g", "I"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
        list.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashMap"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "putIfAbsent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
        list.add(new InsnNode(Opcodes.POP));

        data.method().instructions.insert(list);
    }

    @Override
    public String internal$transformerTarget() {
        return "com/hbm/handler/atmosphere/ChunkAtmosphereHandler";
    }
}
