package xyz.lilyflower.solaris.core.transformers.fml;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.StabilityTransformerSettings;

@SuppressWarnings("unused")
public class FMLControlledNamespacedRegistryTransformer implements SolarisClassTransformer {
    void addObjectRaw(TargetData data) {
        if (StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            data.method().access = Opcodes.ACC_PUBLIC;
        }
    }

    void validateContent(TargetData data) {
        if (StabilityTransformerSettings.STABILITY_OVERRIDES) {
            data.method().instructions.insert(new InsnNode(Opcodes.RETURN));
        }
    }

    @Override
    public String internal$transformerTarget() {
        return "cpw/mods/fml/common/registry/FMLControlledNamespacedRegistry";
    }
}
