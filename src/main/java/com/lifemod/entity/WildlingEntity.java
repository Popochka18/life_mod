package com.lifemod.entity;

import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/** A wildling; appearance depends on the biome the camp spawned in. */
public class WildlingEntity extends FactionVillagerEntity {
    public WildlingEntity(EntityType<? extends WildlingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.WILDLINGS;
    }
}
