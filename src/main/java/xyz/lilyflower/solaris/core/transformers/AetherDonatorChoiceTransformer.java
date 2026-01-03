package xyz.lilyflower.solaris.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;

@SuppressWarnings("unused")
public class AetherDonatorChoiceTransformer implements SolarisClassTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/aetherteam/aether/donator/DonatorChoice";
    }

    void isDonator(TargetData data) {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.IRETURN));
        data.method().instructions.insert(list);
    }
}
