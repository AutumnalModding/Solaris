package xyz.lilyflower.solaris.integration.lotr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import lotr.common.LOTRDimension;
import lotr.common.fac.LOTRControlZone;
import lotr.common.fac.LOTRFaction;
import lotr.common.fac.LOTRFactionRank;
import lotr.common.fac.LOTRFactionRelations;
import lotr.common.fac.LOTRMapRegion;
import lotr.common.world.map.LOTRWaypoint;
import net.minecraftforge.common.util.EnumHelper;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.reflect.EnumHelperMappings;
import xyz.lilyflower.solaris.api.CustomDataLoader;

@SuppressWarnings({"ConstantConditions", "unused"})
public class LOTRCustomFactionLoader implements CustomDataLoader {
    public static final HashMap<LOTRFaction, LOTRFaction> MFRL = new HashMap<>();

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    public void init() {
        File customFactionList = new File(System.getProperty("user.dir") + "/config/solaris/lotr/factions/");

        if (!customFactionList.exists()) {
            customFactionList.mkdirs();
        }

        if (!customFactionList.isDirectory()) {
            customFactionList.delete();
            customFactionList.mkdirs();
        }

        for (String path : customFactionList.list()) {
            try (Stream<String> stream = Files.lines(Paths.get(customFactionList + "/" + path), StandardCharsets.UTF_8)) {
                Field czl = LOTRFaction.class.getDeclaredField("controlZones"); czl.setAccessible(true);
                Field rsd = LOTRFaction.class.getDeclaredField("ranksSortedDescending"); rsd.setAccessible(true);

                Object[] array = stream.toArray();
                int entries = 0;

                String name = "PLACEHOLDER_NAME_THIS_SHOULD_NEVER_APPEAR";
                int colour = Integer.MIN_VALUE;
                LOTRDimension.DimensionRegion region = null;
                LOTRMapRegion map = null;
                EnumSet<LOTRFaction.FactionType> types = null;
                boolean warCrimes = false;
                boolean isolation = false;
                boolean canGetRank = true;

                ArrayList<LOTRControlZone> zones = new ArrayList<>();
                ArrayList<LOTRFactionRankData> data = new ArrayList<>();
                HashMap<LOTRFactionRelations.Relation, ArrayList<LOTRFaction>> relations = new HashMap<>();

                for (Object obj : array) {
                    String line = (String) obj;

                    String key = line.replaceAll(" .*", "");
                    String value = line.replace(key + " ", "");

                    switch (key) {
                        case "NAME":
                            if (name.equals("PLACEHOLDER_NAME_THIS_SHOULD_NEVER_APPEAR")) {
                                name = value;
                                entries++;
                            }
                            break;

                        case "COLOUR":
                            if (colour == Integer.MIN_VALUE) {
                                colour = Integer.parseInt(value.replace("0x", ""), 16);
                                entries++;
                            }
                            break;

                        case "REGION":
                            if (region == null) {
                                region = LOTRDimension.DimensionRegion.valueOf(value);
                                entries++;
                            }
                            break;

                        case "LOCATION":
                            if (map == null) {
                                String[] mapRegionParams = value.split(" ");
                                if (mapRegionParams.length >= 3) {
                                    int x = Integer.parseInt(mapRegionParams[0]);
                                    int y = Integer.parseInt(mapRegionParams[1]);
                                    int r = Integer.parseInt(mapRegionParams[2]);

                                    map = new LOTRMapRegion(x, y, r);
                                }
                                entries++;
                            }

                            break;

                        case "TYPE":
                            if (types == null) {
                                types = EnumSet.noneOf(LOTRFaction.FactionType.class);
                                String[] factionTypes = value.split(" ");
                                for (String type : factionTypes) {
                                    LOTRFaction.FactionType parsed = LOTRFaction.FactionType.valueOf("TYPE_" + type.toUpperCase());
                                    types.add(parsed);
                                }
                                entries++;
                            }

                            break;

                        case "WARCRIMES":
                            warCrimes = Boolean.parseBoolean(value);
                            break;

                        case "ISOLATIONIST":
                            isolation = Boolean.parseBoolean(value);
                            break;

                        case "JOINABLE":
                            canGetRank = Boolean.parseBoolean(value);
                            break;

                        case "CONTROL":
                            String[] zone = value.split(" ");
                            if (zone.length >= 2) {
                                LOTRWaypoint waypoint = LOTRWaypoint.valueOf(zone[0].toUpperCase());
                                int radius = Integer.parseInt(zone[1]);
                                System.out.println(waypoint + ", " + radius);

                                zones.add(new LOTRControlZone(waypoint, radius));
                            }
                            break;

                        case "RANK":
                            String[] rank = value.split(" ");
                            if (rank.length >= 5) {
                                String title = rank[0];
                                int alignment = Integer.parseInt(rank[1]);
                                boolean needsPledge = Boolean.parseBoolean(rank[2]);
                                boolean makeChatTitle = Boolean.parseBoolean(rank[3]);
                                boolean makeAchievement = Boolean.parseBoolean(rank[4]);

                                data.add(new LOTRFactionRankData(title, alignment, needsPledge, makeChatTitle, makeAchievement));
                            }
                            break;

                        case "RELATION":
                            String[] rel = value.split(" ");
                            if (rel.length >= 2) {
                                LOTRFaction target = LOTRFaction.valueOf(rel[0]);
                                LOTRFactionRelations.Relation relation = LOTRFactionRelations.Relation.valueOf(rel[1]);

                                ArrayList<LOTRFaction> factions = relations.getOrDefault(relation, new ArrayList<>());
                                factions.add(target);
                                relations.put(relation, factions);
                            }
                            break;
                    }
                }

                if (entries >= 5) {
                    LOTRFaction faction = EnumHelper.addEnum(EnumHelperMappings.LOTR, LOTRFaction.class, name, colour, LOTRDimension.MIDDLE_EARTH, region, map, types);
                    faction.approvesWarCrimes = warCrimes;
                    faction.isolationist = isolation;
                    faction.allowPlayer = canGetRank;


                    boolean setPledgeRank = false;
                    ((List<LOTRControlZone>) czl.get(faction)).addAll(zones);
                    for (LOTRFactionRankData details : data) {
                        LOTRFactionRank rank = createRank(details, faction);
                        ((List<LOTRFactionRank>) rsd.get(faction)).add(rank);
                        if (!setPledgeRank && details.pledge()) {
                            faction.setPledgeRank(rank);
                            setPledgeRank = true;
                        }
                    }

                    Collections.sort((List<LOTRFactionRank>) rsd.get(faction));
                    relations.forEach((relation, factions) -> factions.forEach(target -> LOTRFactionRelations.setDefaultRelations(faction, target, relation)));
                    Solaris.LOGGER.info("Added faction '{}'", name);
                }

            } catch (IOException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private LOTRFactionRank createRank(LOTRFactionRankData data, LOTRFaction faction) {
        LOTRFactionRank rank = new LOTRFactionRank(faction, data.alignment(), data.name(), false);

        // TODO fix this

        // TODO update from 2025 me: yeah this requires custom achievement category support.
        // TODO: so uhh. implement that I guess lmao

//                        if (data.achievement()) {
//                            rank = rank.achievement();
//                        }

        if (data.title()) {
            rank = rank.makeTitle();
        }

        return rank;
    }
}
