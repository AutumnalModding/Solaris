package xyz.lilyflower.solaris.core.transformers.nevermine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;

@SuppressWarnings("unused")
public class EntityNoExplosionsTransformer implements SolarisClassTransformer {
    void func_70097_a(TargetData data) {
        if (NevermineTransformerSettings.ALLOW_ALL || NevermineTransformerSettings.ALLOW_EXPLOSIONS) {
            InsnList list = new InsnList();

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new VarInsnNode(Opcodes.FLOAD, 2));
            list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/EntityLivingBase", "func_70097_a", "(Lnet/minecraft/util/DamageSource;F)Z", false));
            list.add(new InsnNode(Opcodes.IRETURN));

            data.method().instructions.insert(list);
        }
    }

    void solaris$metadata(ClassNode node) {
        if (NevermineTransformerSettings.ALLOW_ALL || NevermineTransformerSettings.ALLOW_EXPLOSIONS) node.interfaces.remove(this.internal$transformerTarget());
    }

    @Override public String internal$transformerTarget() {
        return "net/nevermine/mob/placement/EntityNoExplosions";
    }
}
