package com.lifemod.client.render;

import com.lifemod.LifeModIds;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
// VERIFY-MAPPING: net.minecraft.client.model.geom.builders.* (LayerDefinition is confirmed)
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.PartPose;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Custom deer model: a slender quadruped with a raised neck, ears, antlers and
 * a short tail. The texture (textures/entity/animal/deer.png, 64x64) is generated
 * to match these exact box UVs.
 */
public class DeerModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(LifeModIds.id("deer"), "main");

    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public DeerModel(ModelPart root) {
        super(root); // VERIFY-MAPPING: EntityModel(ModelPart) constructor
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -6.0F, -9.0F, 8.0F, 6.0F, 18.0F),
                PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 25).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, 8.0F, -7.0F));

        neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(16, 25).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 6.0F)
                .texOffs(44, 25).addBox(-4.0F, -3.0F, -1.0F, 2.0F, 2.0F, 1.0F)
                .texOffs(44, 25).addBox(2.0F, -3.0F, -1.0F, 2.0F, 2.0F, 1.0F)
                .texOffs(44, 30).addBox(-3.0F, -8.0F, -1.0F, 1.0F, 6.0F, 1.0F)
                .texOffs(44, 30).addBox(2.0F, -8.0F, -1.0F, 1.0F, 6.0F, 1.0F),
                PartPose.offset(0.0F, -5.0F, 0.0F));

        root.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(56, 25).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 9.0F));

        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(40, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
                PartPose.offset(-3.0F, 13.0F, -6.0F));
        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(40, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
                PartPose.offset(3.0F, 13.0F, -6.0F));
        root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(40, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
                PartPose.offset(-3.0F, 13.0F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(40, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
                PartPose.offset(3.0F, 13.0F, 7.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        // VERIFY-MAPPING: LivingEntityRenderState#walkAnimationPos / #walkAnimationSpeed
        float swing = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.2F * state.walkAnimationSpeed;
        this.rightFrontLeg.xRot = swing;
        this.leftFrontLeg.xRot = -swing;
        this.rightHindLeg.xRot = -swing;
        this.leftHindLeg.xRot = swing;
    }
}
