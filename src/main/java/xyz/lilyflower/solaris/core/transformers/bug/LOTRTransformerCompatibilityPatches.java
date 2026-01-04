package xyz.lilyflower.solaris.core.transformers.bug;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

@SuppressWarnings("unused")
public class LOTRTransformerCompatibilityPatches implements SolarisClassTransformer {
    void patchBlockFire(TargetData data) {
        data.method().instructions.insert(new InsnNode(Opcodes.ARETURN));
        data.method().instructions.insert(new VarInsnNode(Opcodes.ALOAD, 2));
    }

    void patchSpawnerAnimals(TargetData data) {
        data.method().instructions.insert(new InsnNode(Opcodes.ARETURN));
        data.method().instructions.insert(new VarInsnNode(Opcodes.ALOAD, 2));
    }

    @Override
    public String internal$transformerTarget() {
        return "lotr/common/coremod/LOTRClassTransformer";
    }
}