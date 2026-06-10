package com.lifemod.registry;

import com.lifemod.LifeModIds;
import com.lifemod.item.BlowgunItem;
import com.lifemod.item.DeedItem;
import com.lifemod.item.MusketItem;
import com.lifemod.item.SpearItem;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

/** All mod items. Spears, blowgun, dart and peace axe are ported from Diversity 1.7.2. */
public final class ModItems {
    // VERIFY-MAPPING: Item.Properties#sword / #axe helpers (the #pickaxe analog is confirmed);
    // fallback: plain Item without tool stats.
    public static final Item WOODEN_SPEAR = register("wooden_spear",
            key -> new SpearItem(6.0, new Item.Properties().sword(ToolMaterial.WOOD, 4.0F, -2.6F).setId(key)));
    public static final Item STONE_SPEAR = register("stone_spear",
            key -> new SpearItem(7.0, new Item.Properties().sword(ToolMaterial.STONE, 4.0F, -2.6F).setId(key)));
    public static final Item IRON_SPEAR = register("iron_spear",
            key -> new SpearItem(8.5, new Item.Properties().sword(ToolMaterial.IRON, 4.0F, -2.6F).setId(key)));
    public static final Item GOLDEN_SPEAR = register("golden_spear",
            key -> new SpearItem(7.0, new Item.Properties().sword(ToolMaterial.GOLD, 4.0F, -2.6F).setId(key)));
    public static final Item DIAMOND_SPEAR = register("diamond_spear",
            key -> new SpearItem(10.0, new Item.Properties().sword(ToolMaterial.DIAMOND, 4.0F, -2.6F).setId(key)));

    public static final Item PEACE_AXE = register("peace_axe",
            key -> new Item(new Item.Properties().axe(ToolMaterial.IRON, 5.0F, -3.0F).setId(key)));

    public static final Item BLOWGUN = register("blowgun",
            key -> new BlowgunItem(new Item.Properties().stacksTo(1).durability(192).setId(key)));
    public static final Item DART = register("dart",
            key -> new Item(new Item.Properties().setId(key)));

    public static final Item MUSKET = register("musket",
            key -> new MusketItem(new Item.Properties().stacksTo(1).durability(256).setId(key)));
    public static final Item MUSKET_BALL = register("musket_ball",
            key -> new Item(new Item.Properties().setId(key)));
    public static final Item POWDER_POUCH = register("powder_pouch",
            key -> new Item(new Item.Properties().setId(key)));

    public static final Item HOUSE_DEED = register("house_deed",
            key -> new DeedItem(new Item.Properties().stacksTo(1).setId(key)));

    private ModItems() {
    }

    private static Item register(String name, Function<ResourceKey<Item>, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, LifeModIds.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(key));
    }

    public static void init() {
        // Static initialisation registers the items.
    }
}
