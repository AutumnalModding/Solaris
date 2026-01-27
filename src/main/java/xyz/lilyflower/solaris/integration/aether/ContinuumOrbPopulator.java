package xyz.lilyflower.solaris.integration.aether;

import java.util.ArrayList;
import java.util.List;
import net.aetherteam.aether.items.consumables.ItemContinuum;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.api.SolarisIntegrationModule;
import xyz.lilyflower.solaris.configuration.modules.SolarisAether;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.list.ChainedArrayList;
import xyz.lilyflower.solaris.util.reflect.SolarisReflection;

public class ContinuumOrbPopulator implements SolarisIntegrationModule {
    @Override
    public void run() {
        ArrayList<ItemStack> list = SolarisReflection.get(ItemContinuum.class, "possibleItems");
        list.clear();

        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<ItemStack> blocks = new ArrayList<>();

        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            try {
                item.getSubItems(item, null, items);
            } catch (Exception exception) {
                ItemStack stack = new ItemStack(item, 0);
                Solaris.LOGGER.warn("Failed getting variants for item {} (\"{}\") of type {}! (Reason: {}: {})",
                        item.getUnlocalizedName(),
                        item.getItemStackDisplayName(stack),
                        item.getClass().getName(),
                        exception.getClass().getName(),
                        exception.getMessage()
                );
                items.add(stack);
            }
        }

        for (Object obj : Block.blockRegistry) {
            Block block = (Block) obj;
            Item item = Item.getItemFromBlock(block);
            try {
                if (item != null) {
                    item.getSubItems(item, null, blocks);
                }
            } catch (Exception exception) {
                Solaris.LOGGER.warn("Failed getting variants for block {} (\"{}\") of type {}! (Reason: {}: {})",
                        block.getUnlocalizedName(),
                        block.getLocalizedName(),
                        block.getClass().getName(),
                        exception.getClass().getName(),
                        exception.getMessage()
                );
                blocks.add(new ItemStack(item, 0));
            }
        }

        list.addAll(items);
        list.addAll(blocks);

        SolarisReflection.set(ItemContinuum.class, "possibleItems", list);
        Solaris.LOGGER.info("Found {} items, {} blocks. {} possible Continuum Orb items. Happy gambling!", items.size(), blocks.size(), list.size());
    }

    @Override
    public List<String> requiredMods() {
        return new ChainedArrayList<String>().chain("aether");
    }

    @Override
    public boolean valid() {
        return SolarisAether.CONTINUUM_DANGEROUS && Solaris.STAGE == LoadStage.PRELOADER;
    }
}
