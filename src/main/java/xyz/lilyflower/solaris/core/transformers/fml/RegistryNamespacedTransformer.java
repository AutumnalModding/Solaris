package xyz.lilyflower.solaris.core.transformers.fml;

import org.objectweb.asm.tree.InsnList;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.StabilityTransformerSettings;
import xyz.lilyflower.solaris.util.TransformerMacros;

@SuppressWarnings("unused")
public class RegistryNamespacedTransformer implements SolarisClassTransformer {
    void addObject(TargetData data) {
        if (StabilityTransformerSettings.GROSS_REGISTRY_HACKS) {
            InsnList list = new InsnList();
            TransformerMacros.CancelRegistrationForID(list, 422);
            data.method().instructions.insert(list);
        }
    }

    @Override
    public String internal$transformerTarget() {
        return "net/minecraft/util/RegistryNamespaced";
    }
}
