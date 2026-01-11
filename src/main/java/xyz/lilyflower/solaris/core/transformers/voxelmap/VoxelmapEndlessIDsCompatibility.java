//package xyz.lilyflower.solaris.core.transformers.specific.voxelmap;
//
//import net.minecraft.world.chunk.Chunk;
//import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.tree.ClassNode;
//import org.objectweb.asm.tree.MethodInsnNode;
//import xyz.lilyflower.solaris.api.SolarisClassTransformer;
//import xyz.lilyflower.solaris.util.TransformerMacros;
//
//@SuppressWarnings("unused")
//public class VoxelmapEndlessIDsCompatibility implements SolarisClassTransformer {
//    @Override // obfuscation is nothing to me.
//    public String internal$transformerTarget() {
//        return "com/thevoxelbox/voxelmap/a";
//    }
//
//    void __int(TargetData data) {
//        if (data.method().desc.equals("(II)V")) {
//            TransformerMacros.ReplaceMethodCall(
//                    Chunk.class,
//                    "func_76605_m",
//                    null,
//                    data.method().instructions,
//                    new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "getBiomeShortArray", "()[S", false)
//            );
//
//            for (int i = 0; i < 2; i++) {
//                TransformerMacros.ReplaceMethodCall(
//                        Chunk.class,
//                        "func_76616_a",
//                        new Class<?>[]{byte[].class},
//                        data.method().instructions,
//                        new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "setBiomeShortArray", "([S)V", false)
//                );
//            }
//        }
//    }
//}
