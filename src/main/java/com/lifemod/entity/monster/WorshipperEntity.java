package com.lifemod.entity.monster;

import com.lifemod.LifeModIds;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Mad cultist of the jungle pyramids, ported from Diversity 1.7.2. */
public class WorshipperEntity extends AbstractHostileEntity {
    private static final Identifier TEXTURE = LifeModIds.id("textures/entity/monster/worshipper.png");

    public WorshipperEntity(EntityType<? extends WorshipperEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 3.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
