package xyz.lilyflower.solaris.integration.galacticraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.UUID;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.configuration.modules.SolarisGalacticraft;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.internal.SolarisClassloader;
import xyz.lilyflower.solaris.util.data.Colour;
import xyz.lilyflower.solaris.util.json.ClassTypeAdapter;
import xyz.lilyflower.solaris.util.json.ColourTypeAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlanetParser implements SolarisIntegrationModule {
    public static final HashSet<Star> STARS = new HashSet<>();
    public static SolarSystem ALPHA = !SolarisGalacticraft.DISABLED_CELESTIAL_BODIES.contains("sol")
            ? GalacticraftCore.solarSystemSol
            : new SolarSystem("solaris$alpha", "milkyWay").setMapPosition(new Vector3(0, 0, 0));

    public static SolarSystem BETA = new SolarSystem("solaris$beta", "milkyWay").setMapPosition(new Vector3(-0.55F, 1.0F, 0.0F));

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions", "unchecked"})
    public void run() {
        if (ALPHA != GalacticraftCore.solarSystemSol) GalaxyRegistry.registerSolarSystem(ALPHA);
        GalaxyRegistry.registerSolarSystem(BETA);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeHierarchyAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapter(Colour.class, new ColourTypeAdapter())
                .create();

        File definitions = new File(System.getProperty("user.dir") + "/config/solaris/galacticraft/");
        if (!definitions.isDirectory()) definitions.delete();
        if (!definitions.exists()) definitions.mkdirs();

        for (String path : definitions.list()) {
            Solaris.LOGGER.info("Parsing planet file {}", path);
            try (Stream<String> stream = Files.lines(Paths.get(definitions + "/" + path), StandardCharsets.UTF_8)) {
                String unparsed = stream.collect(Collectors.joining("\n"));
                PlanetData data = gson.fromJson(unparsed, PlanetData.class);

                ClassNode node = new ClassNode();
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                node.version = Opcodes.V1_8;
                node.access = Opcodes.ACC_PUBLIC;
                node.name = UUID.randomUUID().toString();
                node.superName = PlanetProvider.class.getName().replace('.', '/');

                MethodNode method = new MethodNode(
                        Opcodes.ACC_PUBLIC,
                        "<init>",
                        "(Lxyz/lilyflower/solaris/integration/galacticraft/PlanetData;)V",
                        null,
                        null
                );
                InsnList instructions = method.instructions;
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        PlanetProvider.class.getName().replace('.', '/'),
                        "<init>",
                        "(Lxyz/lilyflower/solaris/integration/galacticraft/PlanetData;)V",
                        false
                ));
                instructions.add(new InsnNode(Opcodes.RETURN));
                node.methods.add(method);
                node.accept(writer);
                byte[] bytes = writer.toByteArray();

                Class<SolarisClassloader> loader = SolarisClassloader.class;
                Field field = loader.getDeclaredField("INSTANCE");
                field.setAccessible(true);
                SolarisClassloader instance = (SolarisClassloader) field.get(null);
                Class<? extends PlanetProvider> clazz = (Class<? extends PlanetProvider>) instance.load(node.name, bytes);
                Constructor<? extends PlanetProvider> constructor = clazz.getConstructor(PlanetData.class);
                PlanetProvider provider = constructor.newInstance(data);
                Solaris.LOGGER.info("Registering planet {}!", data.name());
                provider.register();
            } catch (IOException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException |
                     NoSuchMethodException | InstantiationException | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override public boolean valid() { return Solaris.STATE == LoadStage.RUNNING; }
    @Override public List<String> requiredMods() { return Arrays.asList("GalacticraftCore"); }
}
