package xyz.lilyflower.solaris.core.transformers.aetherii;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.AetherIITransformerSettings;
import xyz.lilyflower.solaris.util.TransformerMacros;

@SuppressWarnings("unused")
public class AetherIIModTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/aetherteam/aether/Aether";
    }

    void transferPlayer(TargetData data) {
        InsnList list = new InsnList();
        LabelNode skip = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new JumpInsnNode(Opcodes.IFNE, skip));
        TransformerMacros.GetStaticField(AetherIITransformerSettings.class, "FREEFALL_TARGET", list);
        list.add(new VarInsnNode(Opcodes.ISTORE, 1));
        list.add(skip);

        data.method().instructions.insert(list);
    }
}
