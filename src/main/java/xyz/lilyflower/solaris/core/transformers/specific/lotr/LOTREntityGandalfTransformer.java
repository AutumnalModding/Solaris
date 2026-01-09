package xyz.lilyflower.solaris.core.transformers.specific.lotr;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

@SuppressWarnings("unused")
public class LOTREntityGandalfTransformer implements SolarisClassTransformer {
    void func_70097_a(TargetData data) {
        InsnList list = new InsnList();

        LabelNode label = new LabelNode(new Label());
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "xyz/lilyflower/solaris/configuration/modules/SolarisLOTR", "ENABLE_WANDERER_DEATH", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "lotr/common/entity/npc/LOTREntityNPC", "func_70097_a", "(Lnet/minecraft/util/DamageSource;F)Z", false));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(label);

        data.method().instructions.insert(list);
    }

    @Override
    public String internal$transformerTarget() {
        return "lotr/common/entity/npc/LOTREntityGandalf";
    }
}
