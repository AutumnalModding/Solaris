package xyz.lilyflower.solaris.integration.lotr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.fac.LOTRFaction;
import lotr.common.world.spawning.LOTRInvasions;
import net.minecraftforge.common.util.EnumHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.solaris.util.EnumHelperMappings;
import xyz.lilyflower.solaris.api.CustomDataLoader;

@SuppressWarnings({"ConstantConditions", "unused"})
public class LOTRCustomInvasionLoader implements CustomDataLoader {
    private static final Logger LOGGER = LogManager.getLogger("LOTR Invasion Loader");

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void init() {
        File customInvasionList = new File(System.getProperty("user.dir") + "/config/solaris/lotr/invasions/");

        if (!customInvasionList.exists()) {
            customInvasionList.mkdirs();
        }

        if (!customInvasionList.isDirectory()) {
            customInvasionList.delete();
            customInvasionList.mkdirs();
        }

        for (String path : customInvasionList.list()) {
            try (Stream<String> stream = Files.lines(Paths.get(customInvasionList + "/" + path), StandardCharsets.UTF_8)) {
                Object[] array = stream.toArray();

                if (array.length >= 3) {
                    int lineNo = 1;

                    String invasionName = "PLACEHOLDER_NAME_THIS_SHOULD_NEVER_APPEAR";
                    LOTRFaction invasionFaction = LOTRFaction.UTUMNO;
                    ArrayList<String> invasionSpawns = new ArrayList<>();

                    for (Object obj : array) {
                        String line = (String) obj;

                        switch (lineNo++) {
                            case 1:
                                invasionName = line.toUpperCase();
                                break;

                            case 2:
                                invasionFaction = LOTRFaction.forName(line);
                                invasionFaction = (invasionFaction == null ? LOTRFaction.UTUMNO : invasionFaction);
                                break;

                            default:
                                invasionSpawns.add(line);
                                break;
                        }
                    }

                    LOTRInvasions invasion = EnumHelper.addEnum(EnumHelperMappings.LOTR, LOTRInvasions.class, invasionName, invasionFaction);

                    invasionSpawns.forEach(entry -> {
                        String[] split = entry.split(" ");
                        if (split.length == 2) {
                            try {
                                Class<?> target = Class.forName("lotr.common.entity.npc." + split[0]);
                                int chance = Integer.parseInt(split[1]);

                                if (LOTREntityNPC.class.isAssignableFrom(target)) {
                                    @SuppressWarnings("unchecked") Class<LOTREntityNPC> npc = (Class<LOTREntityNPC>) target;
                                    LOTRInvasions.InvasionSpawnEntry spawnEntry = new LOTRInvasions.InvasionSpawnEntry(npc, chance);
                                    invasion.invasionMobs.add(spawnEntry);
                                }
                            } catch (ClassNotFoundException | NumberFormatException ignored) {}
                        }
                    });

                    LOGGER.info("Added invasion '{}' (faction '{}')", invasionName, invasionFaction);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
