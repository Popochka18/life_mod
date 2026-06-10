package com.lifemod.entity;

import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/** A small, fast and evil goblin. At war with everyone except players. */
public class GoblinEntity extends FactionVillagerEntity {
    public GoblinEntity(EntityType<? extends GoblinEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.ATTACK_DAMAGE, 2.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.GOBLINS;
    }
}
