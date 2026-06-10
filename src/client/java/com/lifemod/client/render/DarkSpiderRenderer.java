package com.lifemod.client.render;

import com.lifemod.entity.monster.DarkSpiderEntity;

import net.minecraft.client.model.geom.ModelLayers;
// VERIFY-MAPPING: spider model package (expect net.minecraft.client.model.monster.spider.SpiderModel)
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
// VERIFY-MAPPING: net.minecraft.client.renderer.entity.state.SpiderRenderState (fallback: LivingEntityRenderState)
import net.minecraft.client.renderer.entity.state.SpiderRenderState;
import net.minecraft.resources.Identifier;

/** Renders the dark spider with the vanilla spider model and a darkened texture. */
public class DarkSpiderRenderer
        extends MobRenderer<DarkSpiderEntity, DarkSpiderRenderer.State, SpiderModel> {
    public DarkSpiderRenderer(EntityRendererProvider.Context context) {
        // VERIFY-MAPPING: ModelLayers.SPIDER; SpiderModel constructor
        super(context, new SpiderModel(context.bakeLayer(ModelLayers.SPIDER)), 0.8f);
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
    public void extractRenderState(DarkSpiderEntity entity, State renderState, float tickProgress) {
        super.extractRenderState(entity, renderState, tickProgress);
        renderState.texture = entity.getTexture();
    }

    public static class State extends SpiderRenderState {
        public Identifier texture;
    }
}
