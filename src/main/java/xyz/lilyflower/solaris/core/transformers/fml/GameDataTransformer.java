package xyz.lilyflower.solaris.core.transformers.fml;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.StabilityTransformerSettings;

@SuppressWarnings("unused")
public class GameDataTransformer implements SolarisClassTransformer {
    void addPrefix(TargetData data) {
        if (data.method().access == Opcodes.ACC_PRIVATE && StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            InsnList insns = new InsnList();
            LabelNode jump = new LabelNode(new Label());

            insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insns.add(new LdcInsnNode("$APPLYPREFIX$"));
            insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));
            insns.add(new JumpInsnNode(Opcodes.IFEQ, jump));
            insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insns.add(new LdcInsnNode("$APPLYPREFIX$"));
            insns.add(new LdcInsnNode(""));
            insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false));
            insns.add(new InsnNode(Opcodes.ARETURN));
            insns.add(jump);

            data.method().instructions.insert(insns);
        }
    }

    void getMain(TargetData data) {
        if (StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            data.method().access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        }
    }

    void registerItem(TargetData data) {
        if (data.method().access == Opcodes.ACC_PRIVATE && StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            InsnList list = new InsnList();
            LabelNode jump = new LabelNode(new Label());

            list.add(new VarInsnNode(Opcodes.ILOAD, 3));
            list.add(new JumpInsnNode(Opcodes.IFLT, jump));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/registry/GameData", "availabilityMap", "Ljava/util/BitSet;"));
            list.add(new VarInsnNode(Opcodes.ILOAD, 3));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/BitSet", "clear", "(I)V", false));
            list.add(jump);

            data.method().instructions.insert(list);
        }

        data.method().access = Opcodes.ACC_PUBLIC;
    }

    void testConsistency(TargetData data) {
        if (StabilityTransformerSettings.STABILITY_OVERRIDES) {
            data.method().instructions.insert(new InsnNode(Opcodes.RETURN));
        }
    }

    @Override
    public String internal$transformerTarget() {
        return "cpw/mods/fml/common/registry/GameData";
    }
}
