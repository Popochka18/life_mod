package com.lifemod.client.render;

import com.lifemod.entity.TexturedMob;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Renders humanoid monsters (mummy, tzitzimime, worshipper, yeti) with the vanilla
 * humanoid (zombie-layout, 64x64) model and the entity-supplied texture.
 */
public class HumanoidMonsterRenderer<T extends Mob & TexturedMob>
        extends MobRenderer<T, HumanoidMonsterRenderer.State, HumanoidModel<HumanoidMonsterRenderer.State>> {
    public HumanoidMonsterRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f);
    }

    public HumanoidMonsterRenderer(EntityRendererProvider.Context context, float scale) {
        // VERIFY-MAPPING: ModelLayers.PLAYER (any humanoid layer works, e.g. ZOMBIE)
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f * scale);
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

    public static class State extends HumanoidRenderState {
        public Identifier texture;
    }
}
