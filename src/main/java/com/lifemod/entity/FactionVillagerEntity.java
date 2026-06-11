package com.lifemod.entity;

import com.lifemod.LifeModIds;
import com.lifemod.entity.ai.FactionTargetGoal;
import com.lifemod.faction.Alignment;
import com.lifemod.faction.Faction;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
// VERIFY-MAPPING: net.minecraft.world.entity.PathfinderMob (fallback: extend Mob directly)
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
// VERIFY-MAPPING: goal classes in net.minecraft.world.entity.ai.goal
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A faction member with a profession. Replaces the per-tribe entities of Diversity 1.7.2
 * (EntityGlobalVillager and friends) with a single entity whose faction, profession and
 * skin are picked from the biome it spawned in.
 */
public class FactionVillagerEntity extends PathfinderMob implements FactionMob, TexturedMob {
    private static final EntityDataAccessor<String> DATA_FACTION =
            SynchedEntityData.defineId(FactionVillagerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PROFESSION =
            SynchedEntityData.defineId(FactionVillagerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_SKIN =
            SynchedEntityData.defineId(FactionVillagerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_ENSLAVED =
            SynchedEntityData.defineId(FactionVillagerEntity.class, EntityDataSerializers.BOOLEAN);

    public FactionVillagerEntity(EntityType<? extends FactionVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 3.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FactionTargetGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FACTION, "");
        builder.define(DATA_PROFESSION, "");
        builder.define(DATA_SKIN, "plains");
        builder.define(DATA_ENSLAVED, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.entityData.get(DATA_FACTION).isEmpty()) {
            this.initializeFromBiome();
        }
    }

    protected void initializeFromBiome() {
        Holder<Biome> biome = this.level().getBiome(this.blockPosition());
        Faction faction = this.defaultFaction(biome);
        this.setFaction(faction);
        this.entityData.set(DATA_SKIN, Faction.skinForBiome(biome));
        this.setProfession(this.pickProfession(faction));
    }

    /** Elves and vegirs use the player model and have their own entity types. */
    private static final java.util.Set<Faction> DEDICATED_FACTIONS =
            java.util.EnumSet.of(Faction.ELVES, Faction.VEGIRS);

    /** Subclasses (leaders, innkeepers, mercenaries) override to pin faction or profession. */
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.pickForBiome(biome, this.getRandom(), DEDICATED_FACTIONS);
    }

    protected String pickProfession(Faction faction) {
        return faction.professions().get(this.getRandom().nextInt(faction.professions().size()));
    }

    @Override
    public Faction getFaction() {
        String name = this.entityData.get(DATA_FACTION);
        return name.isEmpty() ? Faction.VILLAGERS : Faction.byName(name);
    }

    @Override
    public void setFaction(Faction faction) {
        this.entityData.set(DATA_FACTION, faction.getSerializedName());
    }

    public String getProfession() {
        return this.entityData.get(DATA_PROFESSION);
    }

    public void setProfession(String profession) {
        this.entityData.set(DATA_PROFESSION, profession);
    }

    public String getSkin() {
        return this.entityData.get(DATA_SKIN);
    }

    @Override
    public boolean isEnslaved() {
        return this.entityData.get(DATA_ENSLAVED);
    }

    @Override
    public void setEnslaved(boolean enslaved) {
        this.entityData.set(DATA_ENSLAVED, enslaved);
    }

    protected void setSkin(String skin) {
        this.entityData.set(DATA_SKIN, skin);
    }

    @Override
    public Identifier getTexture() {
        Faction faction = this.getFaction();

        // The Villagers faction are the classic vanilla villagers.
        if (faction == Faction.VILLAGERS && !(this instanceof InnkeeperEntity)) {
            return Identifier.withDefaultNamespace("textures/entity/villager/villager.png");
        }

        String profession = this.getProfession().isEmpty() ? "villager" : this.getProfession();
        String path = faction.hasBiomeSkins()
                ? "textures/entity/villager/" + faction.getSerializedName() + "/" + this.getSkin() + "/" + profession + ".png"
                : "textures/entity/villager/" + faction.getSerializedName() + "/" + profession + ".png";
        return LifeModIds.id(path);
    }

    /** GOOD factions never use violence, and no faction NPC ever attacks a player. */
    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.getFaction().alignment() == Alignment.GOOD || this.isEnslaved()) {
            return false;
        }

        if (target instanceof Player) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack held = player.getItemInHand(hand);

        if (this.isEnslaved()) {
            if (held.is(Items.GOLD_INGOT)) {
                held.shrink(1);
                this.setEnslaved(false);
                player.sendSystemMessage(Component.translatable("message.life-mod.slave.freed"));
                return InteractionResult.SUCCESS;
            }

            player.sendSystemMessage(Component.translatable("message.life-mod.slave.greeting"));
            return InteractionResult.SUCCESS;
        }

        if (held.is(Items.EMERALD)) {
            ItemStack offer = FactionTrades.offerFor(this.getProfession());

            if (!offer.isEmpty()) {
                held.shrink(1);
                player.getInventory().placeItemBackInInventory(offer);
                return InteractionResult.SUCCESS;
            }
        }

        player.sendSystemMessage(Component.translatable("message.life-mod.villager.greeting",
                Component.translatable(this.getFaction().translationKey())));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) { // VERIFY-MAPPING: method name
        super.addAdditionalSaveData(output);
        output.putString("LifeFaction", this.entityData.get(DATA_FACTION));
        output.putString("LifeProfession", this.getProfession());
        output.putString("LifeSkin", this.getSkin());
        output.putBoolean("LifeEnslaved", this.isEnslaved());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) { // VERIFY-MAPPING: method name
        super.readAdditionalSaveData(input);
        this.entityData.set(DATA_FACTION, input.getStringOr("LifeFaction", ""));
        this.entityData.set(DATA_PROFESSION, input.getStringOr("LifeProfession", ""));
        this.entityData.set(DATA_SKIN, input.getStringOr("LifeSkin", "plains"));
        this.entityData.set(DATA_ENSLAVED, input.getBooleanOr("LifeEnslaved", false));
    }
}
