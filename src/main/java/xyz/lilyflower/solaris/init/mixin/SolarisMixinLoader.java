package xyz.lilyflower.solaris.init.mixin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;
import xyz.lilyflower.solaris.debug.LoggingHelper;

@SuppressWarnings({"deprecation", ""})
public class SolarisMixinLoader implements IMixinConfigPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Solaris Mixins");
    private static final Path MODS_DIRECTORY_PATH = new File(Launch.minecraftHome, "mods/").toPath();

    @Override public void onLoad(String location) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> ours, Set<String> theirs) {}
    @Override public boolean shouldApplyMixin(String target, String mixin) { return true; }

    @Override
    public List<String> getMixins() {
        final boolean isDevelopmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<MixinTarget> loaded = Arrays.stream(MixinTarget.values())
                .filter(target -> target == MixinTarget.VANILLA
                        || (target.development && isDevelopmentEnvironment)
                        || load(target))
                .collect(Collectors.toList());

        LOGGER.info("Available targets: ");
        for (MixinTarget target : MixinTarget.values()) {
            LOGGER.info("  [{}] {}", loaded.contains(target) ? "X" : " ", target.name);
        }

        List<String> mixins = new ArrayList<>();
        for (MultimodMixin mixin : MultimodMixin.values()) {
            if (mixin.shouldLoad(loaded)) {
                mixins.add(mixin.mixin);
                LOGGER.debug("Loading mixin: {}", mixin.mixin);
            }
        }
        return mixins;
    }

    private boolean load(final MixinTarget target) {
        try {
            File jar = locate(target);
            if (jar == null) return false;
            if (!jar.exists()) throw new FileNotFoundException(jar.toString());

            MinecraftURLClassPath.addJar(jar);
            return true;
        }
        catch (Exception exception) {
            LoggingHelper.oopsie(LOGGER, "FAILED LOADING MIXIN TARGET: " + target, exception);
            return false;
        }
    }

    @SuppressWarnings("resource")
    public static File locate(final MixinTarget target) {
        try {
            return Files.walk(MODS_DIRECTORY_PATH)
                    .filter(target::isMatchingJar)
                    .map(Path::toFile)
                    .findFirst()
                    .orElse(null);
        }
        catch (IOException exception) {
            LoggingHelper.oopsie(LOGGER, "FAILED LOCATING MIXIN TARGET: " + target, exception);
            return null;
        }
    }

    @Override public void preApply(String target, ClassNode clazz, String mixin, IMixinInfo info) {}
    @Override public void postApply(String target, ClassNode clazz, String mixin, IMixinInfo info) {}




}
