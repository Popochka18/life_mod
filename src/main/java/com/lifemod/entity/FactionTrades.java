package com.lifemod.entity;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

/**
 * Simple one-emerald barter table per profession.
 * Distilled from the Diversity 1.7.2 Economy/SupplierTribe trade lists.
 */
public final class FactionTrades {
    private record Offer(Item item, int count) {
    }

    private static final Map<String, Offer> OFFERS = Map.ofEntries(
            Map.entry("farmer", new Offer(Items.BREAD, 3)),
            Map.entry("butcher", new Offer(Items.COOKED_PORKCHOP, 2)),
            Map.entry("librarian", new Offer(Items.BOOK, 1)),
            Map.entry("priest", new Offer(Items.EXPERIENCE_BOTTLE, 1)),
            Map.entry("highpriest", new Offer(Items.EXPERIENCE_BOTTLE, 1)),
            Map.entry("smith", new Offer(Items.IRON_INGOT, 1)),
            Map.entry("guard", new Offer(Items.ARROW, 8)),
            Map.entry("innkeeper", new Offer(Items.COOKED_CHICKEN, 2)),
            Map.entry("villager", new Offer(Items.APPLE, 3)),
            Map.entry("sculptor", new Offer(Items.CHISELED_SANDSTONE, 4)),
            Map.entry("scribe", new Offer(Items.PAPER, 8)),
            Map.entry("painter", new Offer(Items.PAINTING, 1)),
            Map.entry("hunter", new Offer(Items.LEATHER, 2)),
            Map.entry("reindeer_herder", new Offer(Items.LEATHER, 3)),
            Map.entry("dyer", new Offer(Items.RED_DYE, 4)),
            Map.entry("breeder", new Offer(Items.EGG, 4)),
            Map.entry("elf", new Offer(Items.ARROW, 12)),
            Map.entry("amazon", new Offer(Items.BOW, 1)),
            Map.entry("trader", new Offer(Items.GOLD_NUGGET, 6)),
            Map.entry("healer", new Offer(Items.GLISTERING_MELON_SLICE, 1)),
            Map.entry("trapper", new Offer(Items.STRING, 6)),
            Map.entry("miner", new Offer(Items.COAL, 6)),
            Map.entry("cook", new Offer(Items.COOKED_COD, 2)),
            Map.entry("warrior", new Offer(Items.STONE_SWORD, 1)),
            Map.entry("shieldbearer", new Offer(Items.SHIELD, 1)),
            Map.entry("fisherman", new Offer(Items.COOKED_SALMON, 2))
    );

    private static final Offer DEFAULT = new Offer(Items.BREAD, 1);

    private FactionTrades() {
    }

    /** What one emerald buys from a villager with the given profession; empty stack when this profession does not trade. */
    public static ItemStack offerFor(String profession) {
        if (profession.equals("slave") || profession.equals("twisted_villager") || profession.equals("goblin")) {
            return ItemStack.EMPTY;
        }

        Offer offer = OFFERS.getOrDefault(profession, DEFAULT);
        return new ItemStack(offer.item(), offer.count());
    }
}
