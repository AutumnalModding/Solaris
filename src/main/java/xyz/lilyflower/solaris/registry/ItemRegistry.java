package xyz.lilyflower.solaris.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.configuration.modules.SolarisContent;
import xyz.lilyflower.solaris.api.ContentRegistry;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public class ItemRegistry implements ContentRegistry<Item> {
    static final ArrayList<SolarisExtensions.Pair<Item, String>> ITEMS = new ArrayList<>();

    @Override
    public List<SolarisExtensions.Pair<Item, String>> contents() {
        return ITEMS;
    }

    public static final Item STONE_DUST = ContentRegistry.create("dust_stone", Item.class, ContentRegistry.EMPTY, ITEMS);
    public static final Item CHEAP_PLASTIC_PANEL = ContentRegistry.create("plp_cheap", Item.class, ContentRegistry.EMPTY, ITEMS);

    @Override
    public void register(SolarisExtensions.Pair<Item, String> pair) {
        pair.left().setUnlocalizedName("solaris." + pair.right());
        pair.left().setTextureName("solaris:" + pair.right());

        GameRegistry.registerItem(pair.left(), pair.right(), "solaris");
    }

    @Override
    public boolean valid(String key) {
        return SolarisContent.ENABLE_CONTENT;
    }

    @Override
    public boolean runnable() {
        return Solaris.STAGE == LoadStage.PRELOADER;
    }
}
