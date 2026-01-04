package xyz.lilyflower.solaris.core.transformers.nevermine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;

public class EntityHunterTransformer implements SolarisClassTransformer {
    void getLevReq(TargetData data) {
        if (NevermineTransformerSettings.DISABLE_HUNTER_REQUIREMENTS) {
            data.method().instructions.insert(new InsnNode(Opcodes.IRETURN));
            data.method().instructions.insert(new InsnNode(Opcodes.ICONST_0));
        }
    }

    @Override public String internal$transformerTarget() { return "net/nevermine/mob/placement/EntityHunter"; }
}
