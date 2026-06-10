package com.lifemod.entity;

import com.lifemod.entity.ai.FollowHirerGoal;
import com.lifemod.faction.Alignment;
import com.lifemod.faction.Faction;
import com.lifemod.faction.ReputationApi;
import com.lifemod.util.InventoryUtil;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A mercenary that can be hired with emeralds in villages of NEUTRAL and EVIL factions
 * (good factions rely on them for defence). Tavern mercenaries charge a lot more.
 */
public class MercenaryEntity extends FactionVillagerEntity {
    public static final int BASE_PRICE = 8;
    public static final int TAVERN_PRICE = 24;

    private UUID hirerId;
    private boolean tavern;

    public MercenaryEntity(EntityType<? extends MercenaryEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowHirerGoal(this, 1.1));
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        List<Faction> candidates = new ArrayList<>();

        for (Faction faction : Faction.values()) {
            if (faction.alignment() != Alignment.GOOD && faction.likesBiome(biome)) {
                candidates.add(faction);
            }
        }

        if (candidates.isEmpty()) {
            return Faction.MERCENARIES;
        }

        return candidates.get(this.getRandom().nextInt(candidates.size()));
    }

    @Override
    protected String pickProfession(Faction faction) {
        return "mercenary";
    }

    /** Mercenaries always look like mercenaries, whichever faction they serve. */
    @Override
    public net.minecraft.resources.Identifier getTexture() {
        return com.lifemod.LifeModIds.id("textures/entity/villager/mercenaries/" + this.getSkin() + "/mercenary.png");
    }

    public Player getHirer() {
        if (this.hirerId == null) {
            return null;
        }

        return this.level().getPlayerByUUID(this.hirerId); // VERIFY-MAPPING: Level#getPlayerByUUID
    }

    public boolean isHired() {
        return this.hirerId != null;
    }

    public void setTavern(boolean tavern) {
        this.tavern = tavern;
    }

    public boolean isTavern() {
        return this.tavern;
    }

    public int price() {
        return this.tavern ? TAVERN_PRICE : BASE_PRICE;
    }

    @Override
    public void aiStep() { // VERIFY-MAPPING: Mob#aiStep
        super.aiStep();

        if (this.level().isClientSide() || this.tickCount % 20 != 0 || !this.isHired()) {
            return;
        }

        Player hirer = this.getHirer();

        if (hirer == null || !hirer.isAlive()) {
            return;
        }

        LivingEntity threat = hirer.getLastHurtByMob(); // VERIFY-MAPPING: LivingEntity#getLastHurtByMob

        if (threat == null || !threat.isAlive() || threat instanceof Player) {
            threat = hirer.getLastHurtMob(); // VERIFY-MAPPING: LivingEntity#getLastHurtMob
        }

        if (threat != null && threat.isAlive() && !(threat instanceof Player)
                && threat != this && this.distanceToSqr(threat) < 24 * 24) {
            this.setTarget(threat);
        }
    }

    /** Hired mercenaries fight whoever threatens their employer, players excluded. */
    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player) {
            return false;
        }

        if (this.isHired()) {
            return true;
        }

        return super.canAttack(target);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        if (this.isHired()) {
            if (player.getUUID().equals(this.hirerId)) {
                if (player.isShiftKeyDown()) { // VERIFY-MAPPING: Player#isShiftKeyDown
                    this.hirerId = null;
                    serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.dismissed"));
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.following"));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.taken"));
            }

            return InteractionResult.SUCCESS;
        }

        Faction faction = this.getFaction();

        if (!ReputationApi.canHireMercenary(serverPlayer, faction)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.reputation",
                    ReputationApi.REP_HIRE_MERCENARY, Component.translatable(faction.translationKey())));
            return InteractionResult.SUCCESS;
        }

        if (!InventoryUtil.consume(player, Items.EMERALD, this.price())) {
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.price", this.price()));
            return InteractionResult.SUCCESS;
        }

        this.hirerId = player.getUUID();
        this.setPersistenceRequired(); // VERIFY-MAPPING: Mob#setPersistenceRequired
        serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.mercenary.hired"));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("LifeTavern", this.tavern);

        if (this.hirerId != null) {
            output.putString("LifeHirer", this.hirerId.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.tavern = input.getBooleanOr("LifeTavern", false);
        String hirer = input.getStringOr("LifeHirer", "");
        this.hirerId = hirer.isEmpty() ? null : UUID.fromString(hirer);
    }
}
