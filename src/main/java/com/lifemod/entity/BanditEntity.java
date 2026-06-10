package com.lifemod.entity;

import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * A bandit. Bandits no longer attack players by default; their ataman gives kill quests,
 * and raid survivors are enslaved (see FactionEvents).
 */
public class BanditEntity extends FactionVillagerEntity {
    public BanditEntity(EntityType<? extends BanditEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.BANDITS;
    }
}
