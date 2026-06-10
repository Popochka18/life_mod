package com.lifemod.entity;

import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/** A pirate from a beach village, pirate beacon or ship. */
public class PirateEntity extends FactionVillagerEntity {
    public PirateEntity(EntityType<? extends PirateEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.PIRATES;
    }

    @Override
    protected String pickProfession(Faction faction) {
        // Musketeers are a separate entity type with their own AI.
        String profession = super.pickProfession(faction);
        return profession.equals("musketeer") ? "pirate" : profession;
    }
}
