package com.lifemod.entity;

import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * A jungle elf. Rendered with the player model and the uploaded elf skins.
 * Roughly one in eight elves is the settlement owner and gives quests.
 */
public class ElfEntity extends FactionVillagerEntity {
    public ElfEntity(EntityType<? extends ElfEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.ELVES;
    }

    @Override
    protected String pickProfession(Faction faction) {
        if (this.getRandom().nextInt(8) == 0) {
            return faction.leaderProfession();
        }

        return super.pickProfession(faction);
    }

    public boolean isLeader() {
        return this.getProfession().equals(this.getFaction().leaderProfession());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isLeader()) {
            return FactionLeaderEntity.leaderInteract(this, player, hand);
        }

        return super.mobInteract(player, hand);
    }
}
