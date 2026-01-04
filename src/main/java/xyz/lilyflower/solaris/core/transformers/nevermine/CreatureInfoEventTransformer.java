package xyz.lilyflower.solaris.core.transformers.nevermine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.core.settings.modules.NevermineTransformerSettings;

@SuppressWarnings("unused")
public class CreatureInfoEventTransformer implements SolarisClassTransformer {
    void render(TargetData data) {
        if (NevermineTransformerSettings.DISABLE_RENDERING) {
            data.method().instructions.insert(new InsnNode(Opcodes.RETURN));
        }
    }

    @Override
    public String internal$transformerTarget() {
        return "net/nevermine/event/creature/CreatureInfoEvent";
    }
}
