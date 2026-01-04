package xyz.lilyflower.solaris.core.transformers.nevermine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;

public class EntityNoMagicTransformer implements SolarisClassTransformer {
    void func_70097_a(TargetData data) {
        if (NevermineTransformerSettings.ALLOW_ALL || NevermineTransformerSettings.ALLOW_MAGIC) {
            InsnList list = new InsnList();

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new VarInsnNode(Opcodes.FLOAD, 2));
            list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/EntityLivingBase", "func_70097_a", "(Lnet/minecraft/util/DamageSource;F)Z", false));
            list.add(new InsnNode(Opcodes.IRETURN));

            data.method().instructions.insert(list);
        }
    }

    @Override public String internal$transformerTarget() {
        return "net/nevermine/mob/placement/EntityNoMagic";
    }
}
