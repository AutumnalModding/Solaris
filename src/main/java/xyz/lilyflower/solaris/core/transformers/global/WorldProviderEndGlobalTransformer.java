package xyz.lilyflower.solaris.core.transformers.global;

import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.api.SolarisGlobalTransformer;

@SuppressWarnings("unused")
public class WorldProviderEndGlobalTransformer implements SolarisGlobalTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/minecraft/world/WorldProviderEnd";
    }

    void getRespawnDimension(SolarisClassTransformer.TargetData data) {
        WorldProviderGlobalTransformer.invoke(data);
    }
}
