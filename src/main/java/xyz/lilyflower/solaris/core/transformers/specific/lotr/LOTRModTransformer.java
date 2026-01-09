package xyz.lilyflower.solaris.core.transformers.specific.lotr;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.StabilityTransformerSettings;
import xyz.lilyflower.solaris.util.TransformerMacros;

@SuppressWarnings("unused")
public class LOTRModTransformer implements SolarisClassTransformer {
    void registerItem(TargetData data) {
        if (StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            InsnList list = new InsnList();
            LabelNode jump = new LabelNode(new Label());

            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "xyz/lilyflower/solaris/configuration/modules/SolarisContent", "ENABLE_SUBSTITUTIONS_ITEM", "Z"));
            list.add(new JumpInsnNode(Opcodes.IFEQ, jump));
            TransformerMacros.PrepareItemForRegister(list, jump, "minecraft:command_block_minecart", "diamond", "lotr/common/LOTRMod", true);
            list.add(new InsnNode(Opcodes.SWAP));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "cpw/mods/fml/common/registry/GameData", "getItemRegistry", "()Lcpw/mods/fml/common/registry/FMLControlledNamespacedRegistry;", false));
            list.add(new IntInsnNode(Opcodes.SIPUSH, 422));
            list.add(new LdcInsnNode("minecraft:command_block_minecart"));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/registry/FMLControlledNamespacedRegistry", "addObjectRaw", "(ILjava/lang/String;Ljava/lang/Object;)V", false));
            list.add(new InsnNode(Opcodes.RETURN));
            list.add(jump);

            data.method().instructions.insert(list);
        }
    }

    @Override
    public String internal$transformerTarget() {
        return "lotr/common/LOTRMod";
    }
}
