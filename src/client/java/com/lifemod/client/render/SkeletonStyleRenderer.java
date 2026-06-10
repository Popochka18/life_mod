package com.lifemod.client.render;

import com.lifemod.entity.TexturedMob;

import net.minecraft.client.model.geom.ModelLayers;
// VERIFY-MAPPING: skeleton model package (expect net.minecraft.client.model.monster.skeleton.SkeletonModel)
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
// VERIFY-MAPPING: net.minecraft.client.renderer.entity.state.SkeletonRenderState
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/** Renders the warrior skeleton (64x32 skeleton-layout texture from Diversity 1.7.2). */
public class SkeletonStyleRenderer<T extends Mob & TexturedMob>
        extends MobRenderer<T, SkeletonStyleRenderer.State, SkeletonModel> {
    public SkeletonStyleRenderer(EntityRendererProvider.Context context) {
        // VERIFY-MAPPING: ModelLayers.SKELETON; SkeletonModel constructor
        super(context, new SkeletonModel(context.bakeLayer(ModelLayers.SKELETON)), 0.5f);
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

    public static class State extends SkeletonRenderState {
        public Identifier texture;
    }
}
