package com.lifemod.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
// VERIFY-MAPPING: net.minecraft.client.renderer.entity.state.ArrowRenderState
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
// VERIFY-MAPPING: net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.AbstractArrow;

/**
 * Arrow-style renderer with a fixed texture, used for spears, darts and musket balls.
 *
 * VERIFY-MAPPING: vanilla ArrowRenderer/ArrowRenderState names. If they differ, the
 * simplest fix is to mirror whatever vanilla's TippableArrowRenderer does on this version.
 */
public class SimpleArrowRenderer<T extends AbstractArrow> extends ArrowRenderer<T, ArrowRenderState> {
    private final Identifier texture;

    public SimpleArrowRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState renderState) {
        return this.texture;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }
}
