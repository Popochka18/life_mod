package com.lifemod.registry;

import net.minecraft.world.entity.MobCategory;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;

/**
 * Natural spawns per biome. Faction villages/taverns are planned as structures later;
 * until then faction members spawn rarely in their home biomes so the systems can be played.
 */
public final class ModSpawns {
    private ModSpawns() {
    }

    public static void init() {
        // --- Hostile mobs ---
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_MOUNTAIN),
                MobCategory.MONSTER, ModEntities.YETI, 20, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER, ModEntities.DARK_SPIDER, 25, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_DESERT),
                MobCategory.MONSTER, ModEntities.MUMMY, 40, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_FOREST),
                MobCategory.MONSTER, ModEntities.WARRIOR_SKELETON, 15, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_JUNGLE),
                MobCategory.MONSTER, ModEntities.TZITZIMIME, 15, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_JUNGLE),
                MobCategory.MONSTER, ModEntities.WORSHIPPER, 20, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_UNDERGROUND),
                MobCategory.MONSTER, ModEntities.GOBLIN, 25, 2, 4);

        // --- Animals ---
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_FOREST),
                MobCategory.CREATURE, ModEntities.DEER, 12, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_TAIGA),
                MobCategory.CREATURE, ModEntities.DEER, 12, 2, 4);

        // --- Faction members (rare, until faction villages generate as structures) ---
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.FACTION_VILLAGER, 6, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.FACTION_LEADER, 1, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.MERCENARY, 2, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.INNKEEPER, 1, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.BANDIT, 4, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                MobCategory.CREATURE, ModEntities.WILDLING, 4, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_BEACH),
                MobCategory.CREATURE, ModEntities.PIRATE, 15, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_BEACH),
                MobCategory.CREATURE, ModEntities.MUSKETEER, 6, 1, 1);
    }
}
