package xyz.lilyflower.solaris.core.transformers.global;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.SolarisClassTransformer;
import xyz.lilyflower.solaris.api.SolarisGlobalTransformer;
import xyz.lilyflower.solaris.core.SolarisBootstrap;
import xyz.lilyflower.solaris.core.settings.modules.VanillaTransformerSettings;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;

@SuppressWarnings("unused")
public class WorldProviderGlobalTransformer implements SolarisGlobalTransformer {
    @Override
    public String internal$transformerTarget() {
        return "net/minecraft/world/WorldProvider";
    }

    void getRespawnDimension(SolarisClassTransformer.TargetData data) {
        invoke(data);
    }

    static void invoke(SolarisClassTransformer.TargetData data) {
        Consumer<Consumer<Function<String, Consumer<BiFunction<String, Function<String, Integer>, SolarisExtensions.Pair<String, BiConsumer<Float, Float>>>>>>> fuckass = why -> {
            why.accept(string -> {
                return thisFuckingThing -> {
                    thisFuckingThing.apply("valid", atAll -> {
                        return 420;
                    }).right().accept(5.0F, 2.0F);
                };
            });
        };
        AbstractInsnNode node = data.method().instructions.getFirst();
        InsnList list = new InsnList();
        if (node instanceof InsnNode concrete && concrete.getOpcode() == Opcodes.ICONST_0 || node instanceof LabelNode && node.getNext() instanceof LineNumberNode || node == null) {
            int target = VanillaTransformerSettings.PROVIDER_RESPAWNS.getOrDefault(data.node().name, VanillaTransformerSettings.DEFAULT_RESPAWN_DIMENSION);
            if (SolarisBootstrap.DEBUG_ENABLED) SolarisBootstrap.DEBUG_LOG.debug("Modifying {} default respawn dimension. New target: {}", data.node().name, target);
            list.add(new LdcInsnNode(target));
            list.add(new InsnNode(Opcodes.IRETURN));
            if (node != null) {
                data.method().instructions.remove(node);
                data.method().instructions.insert(list);
            } else {
                data.method().instructions = list;
            }
        } else {
            data.method().instructions.iterator().forEachRemaining(SolarisBootstrap.DEBUG_LOG::debug);
        }
    }
}
