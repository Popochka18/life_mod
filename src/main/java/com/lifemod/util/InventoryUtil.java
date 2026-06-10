package com.lifemod.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Inventory helpers used by quests, hiring and the musket. */
public final class InventoryUtil {
    private InventoryUtil() {
    }

    public static int count(Player player, Item item) {
        Inventory inventory = player.getInventory();
        int total = 0;

        // VERIFY-MAPPING: Container#getContainerSize / #getItem
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (stack.is(item)) {
                total += stack.getCount();
            }
        }

        return total;
    }

    /** Removes {@code amount} items from the player's inventory; returns false (and removes nothing) if there are not enough. */
    public static boolean consume(Player player, Item item, int amount) {
        if (count(player, item) < amount) {
            return false;
        }

        Inventory inventory = player.getInventory();
        int remaining = amount;

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (stack.is(item)) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }

        return true;
    }

    public static void give(Player player, ItemStack stack) {
        player.getInventory().placeItemBackInInventory(stack);
    }
}
