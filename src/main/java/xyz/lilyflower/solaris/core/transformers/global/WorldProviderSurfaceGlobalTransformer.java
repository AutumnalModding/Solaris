package xyz.lilyflower.solaris.core.transformers.global;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.api.SolarisGlobalTransformer;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.core.settings.modules.VanillaTransformerSettings;

@SuppressWarnings("unused")
public class WorldProviderSurfaceGlobalTransformer implements SolarisGlobalTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/minecraft/world/WorldProviderSurface";
    }

    void getRespawnDimension(SolarisClassTransformer.TargetData data) {
        WorldProviderGlobalTransformer.invoke(data);
    }
}
