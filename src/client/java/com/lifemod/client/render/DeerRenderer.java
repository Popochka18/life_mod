package com.lifemod.client.render;

import com.lifemod.entity.passive.DeerEntity;

import net.minecraft.client.model.geom.ModelLayers;
// VERIFY-MAPPING: cow model package, expect net.minecraft.client.model.animal.cow.AdultCowModel
// (follows the verified chicken pattern: net.minecraft.client.model.animal.chicken.AdultChickenModel)
import net.minecraft.client.model.animal.cow.AdultCowModel;
import net.minecraft.client.model.animal.cow.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
// VERIFY-MAPPING: net.minecraft.client.renderer.entity.state.CowRenderState
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.resources.Identifier;

/** Placeholder deer renderer on the vanilla cow model; a custom model is future work. */
public class DeerRenderer extends MobRenderer<DeerEntity, DeerRenderer.State, CowModel> {
    public DeerRenderer(EntityRendererProvider.Context context) {
        // VERIFY-MAPPING: ModelLayers.COW
        super(context, new AdultCowModel(context.bakeLayer(ModelLayers.COW)), 0.6f);
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
    public void extractRenderState(DeerEntity entity, State renderState, float tickProgress) {
        super.extractRenderState(entity, renderState, tickProgress);
        renderState.texture = entity.getTexture();
    }

    public static class State extends CowRenderState {
        public Identifier texture;
    }
}
