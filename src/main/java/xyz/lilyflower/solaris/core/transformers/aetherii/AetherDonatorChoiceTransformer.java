package xyz.lilyflower.solaris.core.transformers.aetherii;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

@SuppressWarnings("unused")
public class AetherDonatorChoiceTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/aetherteam/aether/donator/DonatorChoice";
    }

    void isDonator(TargetData data) {
        data.method().instructions.insert(new InsnNode(Opcodes.IRETURN));
        data.method().instructions.insert(new InsnNode(Opcodes.ICONST_1));
    }
}
