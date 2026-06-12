package com.lifemod.client.render;

import com.lifemod.LifeModIds;
import com.lifemod.registry.ModEntities;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

/** Registers a renderer for every mod entity. */
public final class LifeModClientRenderers {
    private LifeModClientRenderers() {
    }

    public static void init() {
        // Custom model layers.
        ModelLayerRegistry.registerModelLayer(DeerModel.LAYER, DeerModel::createBodyLayer);

        // Faction NPCs — villager model, texture picked by the entity.
        EntityRendererRegistry.register(ModEntities.FACTION_VILLAGER, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.FACTION_LEADER, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.INNKEEPER, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.MERCENARY, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.BANDIT, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.WILDLING, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.PIRATE, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUSKETEER, FactionNpcRenderer::new);
        EntityRendererRegistry.register(ModEntities.GOBLIN, FactionNpcRenderer::new);

        // Elves and vegirs use the player model with player-format skins.
        EntityRendererRegistry.register(ModEntities.ELF, HumanoidMonsterRenderer::new);
        EntityRendererRegistry.register(ModEntities.VEGIR, HumanoidMonsterRenderer::new);

        // Monsters.
        EntityRendererRegistry.register(ModEntities.YETI, HumanoidMonsterRenderer::new);
        EntityRendererRegistry.register(ModEntities.MUMMY, HumanoidMonsterRenderer::new);
        EntityRendererRegistry.register(ModEntities.TZITZIMIME, HumanoidMonsterRenderer::new);
        EntityRendererRegistry.register(ModEntities.WORSHIPPER, HumanoidMonsterRenderer::new);
        EntityRendererRegistry.register(ModEntities.WARRIOR_SKELETON, SkeletonStyleRenderer::new);
        EntityRendererRegistry.register(ModEntities.DARK_SPIDER, DarkSpiderRenderer::new);

        // Animals.
        EntityRendererRegistry.register(ModEntities.DEER, DeerRenderer::new);

        // Projectiles.
        EntityRendererRegistry.register(ModEntities.SPEAR,
                context -> new SimpleArrowRenderer<>(context, LifeModIds.id("textures/entity/projectile/spear.png")));
        EntityRendererRegistry.register(ModEntities.DART,
                context -> new SimpleArrowRenderer<>(context, LifeModIds.id("textures/entity/projectile/dart.png")));
        EntityRendererRegistry.register(ModEntities.MUSKET_BALL,
                context -> new SimpleArrowRenderer<>(context, LifeModIds.id("textures/entity/projectile/musket_ball.png")));
    }
}
