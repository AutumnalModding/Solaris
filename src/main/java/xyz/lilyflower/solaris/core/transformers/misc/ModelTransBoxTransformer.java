package xyz.lilyflower.solaris.core.transformers.misc;

import xyz.lilyflower.solaris.api.SolarisClassTransformer;


@SuppressWarnings("unused")
public class ModelTransBoxTransformer implements SolarisClassTransformer {
    void solaris$metadata(TargetData data) {
        data.node().superName = "net/minecraft/client/model/ModelBox";
    }

    @Override
    public String internal$transformerTarget() {
        return "com/unascribed/ears/ModelTransBox";
    }
}
