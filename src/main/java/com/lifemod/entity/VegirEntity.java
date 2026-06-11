package com.lifemod.entity;

import com.lifemod.LifeModIds;
import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * A vegir from the taiga and snowy lands. Rendered with the player model;
 * each vegir picks a random skin from the peasant or warrior pool.
 * Roughly one in eight vegirs is the chief and gives quests.
 */
public class VegirEntity extends FactionVillagerEntity {
    private static final int PEASANT_VARIANTS = 14;
    private static final int WARRIOR_VARIANTS = 5;

    public VegirEntity(EntityType<? extends VegirEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.VEGIRS;
    }

    @Override
    protected void initializeFromBiome() {
        super.initializeFromBiome();
        int variants = this.usesWarriorPool() ? WARRIOR_VARIANTS : PEASANT_VARIANTS;
        this.setSkin(Integer.toString(this.getRandom().nextInt(variants)));
    }

    @Override
    protected String pickProfession(Faction faction) {
        if (this.getRandom().nextInt(8) == 0) {
            return faction.leaderProfession();
        }

        return super.pickProfession(faction);
    }

    private boolean usesWarriorPool() {
        String profession = this.getProfession();
        return profession.equals("warrior") || profession.equals(this.getFaction().leaderProfession());
    }

    public boolean isLeader() {
        return this.getProfession().equals(this.getFaction().leaderProfession());
    }

    @Override
    public Identifier getTexture() {
        String pool = this.usesWarriorPool() ? "warrior" : "peasant";
        int variants = this.usesWarriorPool() ? WARRIOR_VARIANTS : PEASANT_VARIANTS;
        int variant;

        try {
            variant = Math.floorMod(Integer.parseInt(this.getSkin()), variants);
        } catch (NumberFormatException e) {
            variant = 0;
        }

        return LifeModIds.id("textures/entity/villager/vegirs/" + pool + "/" + variant + ".png");
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isLeader()) {
            return FactionLeaderEntity.leaderInteract(this, player, hand);
        }

        return super.mobInteract(player, hand);
    }
}
