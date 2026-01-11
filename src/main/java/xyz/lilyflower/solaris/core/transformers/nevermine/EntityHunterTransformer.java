package xyz.lilyflower.solaris.core.transformers.nevermine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;

@SuppressWarnings("unused")
public class EntityHunterTransformer implements SolarisClassTransformer {
    void getLevReq(TargetData data) {
        if (NevermineTransformerSettings.DISABLE_HUNTER_REQUIREMENTS) {
            data.method().instructions.insert(new InsnNode(Opcodes.IRETURN));
            data.method().instructions.insert(new InsnNode(Opcodes.ICONST_0));
        }
    }

    void solaris$metadata(ClassNode node) {
        if (NevermineTransformerSettings.DISABLE_HUNTER_REQUIREMENTS) {
            node.interfaces.remove(this.internal$transformerTarget());
        }

    }

    @Override public String internal$transformerTarget() { return "net/nevermine/mob/placement/EntityHunter"; }
}
