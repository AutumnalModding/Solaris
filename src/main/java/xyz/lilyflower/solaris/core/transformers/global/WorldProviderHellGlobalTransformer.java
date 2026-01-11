package xyz.lilyflower.solaris.core.transformers.global;

import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.api.SolarisGlobalTransformer;

@SuppressWarnings("unused")
public class WorldProviderHellGlobalTransformer implements SolarisGlobalTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/minecraft/world/WorldProviderHell";
    }

    void getRespawnDimension(SolarisClassTransformer.TargetData data) {
        WorldProviderGlobalTransformer.invoke(data);
    }
}
