package com.lifemod.client.render;

import com.lifemod.entity.TexturedMob;

import net.minecraft.client.model.geom.ModelLayers;
// VERIFY-MAPPING: villager model package (per-mob subpackages, e.g. net.minecraft.client.model.npc.villager.VillagerModel)
import net.minecraft.client.model.npc.villager.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
// VERIFY-MAPPING: net.minecraft.client.renderer.entity.state.VillagerRenderState
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Renders every faction NPC (villagers, leaders, mercenaries, bandits, pirates, goblins...)
 * with the vanilla villager model and a texture chosen by the entity itself
 * (faction + profession + biome skin). Textures ported from Diversity 1.7.2.
 */
public class FactionNpcRenderer<T extends Mob & TexturedMob>
        extends MobRenderer<T, FactionNpcRenderer.State, VillagerModel> {
    public FactionNpcRenderer(EntityRendererProvider.Context context) {
        // VERIFY-MAPPING: ModelLayers.VILLAGER; VillagerModel constructor
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(State renderState) {
        return renderState.texture;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State renderState, float tickProgress) {
        super.extractRenderState(entity, renderState, tickProgress);
        renderState.texture = entity.getTexture();
    }

    public static class State extends VillagerRenderState {
        public Identifier texture;
    }
}
