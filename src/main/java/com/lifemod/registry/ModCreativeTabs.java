package com.lifemod.registry;

import com.lifemod.LifeModIds;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

/** The mod's creative tab. */
public final class ModCreativeTabs {
    public static final ResourceKey<CreativeModeTab> MAIN =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, LifeModIds.id("main"));

    private ModCreativeTabs() {
    }

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.life-mod.main"))
                .icon(() -> new ItemStack(ModItems.MUSKET))
                .displayItems((context, entries) -> {
                    entries.accept(ModItems.WOODEN_SPEAR);
                    entries.accept(ModItems.STONE_SPEAR);
                    entries.accept(ModItems.IRON_SPEAR);
                    entries.accept(ModItems.GOLDEN_SPEAR);
                    entries.accept(ModItems.DIAMOND_SPEAR);
                    entries.accept(ModItems.PEACE_AXE);
                    entries.accept(ModItems.BLOWGUN);
                    entries.accept(ModItems.DART);
                    entries.accept(ModItems.MUSKET);
                    entries.accept(ModItems.MUSKET_BALL);
                    entries.accept(ModItems.POWDER_POUCH);
                    entries.accept(ModItems.HOUSE_DEED);
                })
                .build());
    }
}
