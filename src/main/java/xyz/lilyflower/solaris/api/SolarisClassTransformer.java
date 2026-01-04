package xyz.lilyflower.solaris.api;

import com.github.bsideup.jabel.Desugar;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface SolarisClassTransformer {
    @Desugar
    record TargetData(ClassNode node, MethodNode method) {}

    String internal$transformerTarget();

}
