package com.lifemod.registry;

import com.lifemod.LifeModIds;
import com.lifemod.entity.BanditEntity;
import com.lifemod.entity.ElfEntity;
import com.lifemod.entity.FactionLeaderEntity;
import com.lifemod.entity.VegirEntity;
import com.lifemod.entity.FactionVillagerEntity;
import com.lifemod.entity.GoblinEntity;
import com.lifemod.entity.InnkeeperEntity;
import com.lifemod.entity.MercenaryEntity;
import com.lifemod.entity.MusketeerEntity;
import com.lifemod.entity.PirateEntity;
import com.lifemod.entity.WildlingEntity;
import com.lifemod.entity.monster.DarkSpiderEntity;
import com.lifemod.entity.monster.MummyEntity;
import com.lifemod.entity.monster.TzitzimimeEntity;
import com.lifemod.entity.monster.WarriorSkeletonEntity;
import com.lifemod.entity.monster.WorshipperEntity;
import com.lifemod.entity.monster.YetiEntity;
import com.lifemod.entity.passive.DeerEntity;
import com.lifemod.entity.projectile.DartEntity;
import com.lifemod.entity.projectile.MusketBallEntity;
import com.lifemod.entity.projectile.SpearEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/** All mod entity types. */
public final class ModEntities {
    public static final EntityType<FactionVillagerEntity> FACTION_VILLAGER = registerMob("faction_villager",
            FactionVillagerEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<FactionLeaderEntity> FACTION_LEADER = registerMob("faction_leader",
            FactionLeaderEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<InnkeeperEntity> INNKEEPER = registerMob("innkeeper",
            InnkeeperEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<MercenaryEntity> MERCENARY = registerMob("mercenary",
            MercenaryEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<BanditEntity> BANDIT = registerMob("bandit",
            BanditEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<WildlingEntity> WILDLING = registerMob("wildling",
            WildlingEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<PirateEntity> PIRATE = registerMob("pirate",
            PirateEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<MusketeerEntity> MUSKETEER = registerMob("musketeer",
            MusketeerEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<ElfEntity> ELF = registerMob("elf",
            ElfEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<VegirEntity> VEGIR = registerMob("vegir",
            VegirEntity::new, MobCategory.CREATURE, 0.6f, 1.95f,
            FactionVillagerEntity::createAttributes);
    public static final EntityType<GoblinEntity> GOBLIN = registerMob("goblin",
            GoblinEntity::new, MobCategory.MONSTER, 0.5f, 1.2f,
            GoblinEntity::createAttributes);

    public static final EntityType<YetiEntity> YETI = registerMob("yeti",
            YetiEntity::new, MobCategory.MONSTER, 1.4f, 2.7f,
            YetiEntity::createAttributes);
    public static final EntityType<DarkSpiderEntity> DARK_SPIDER = registerMob("dark_spider",
            DarkSpiderEntity::new, MobCategory.MONSTER, 1.4f, 0.9f,
            DarkSpiderEntity::createAttributes);
    public static final EntityType<MummyEntity> MUMMY = registerMob("mummy",
            MummyEntity::new, MobCategory.MONSTER, 0.6f, 1.95f,
            MummyEntity::createAttributes);
    public static final EntityType<WarriorSkeletonEntity> WARRIOR_SKELETON = registerMob("warrior_skeleton",
            WarriorSkeletonEntity::new, MobCategory.MONSTER, 0.6f, 1.99f,
            WarriorSkeletonEntity::createAttributes);
    public static final EntityType<TzitzimimeEntity> TZITZIMIME = registerMob("tzitzimime",
            TzitzimimeEntity::new, MobCategory.MONSTER, 0.7f, 2.1f,
            TzitzimimeEntity::createAttributes);
    public static final EntityType<WorshipperEntity> WORSHIPPER = registerMob("worshipper",
            WorshipperEntity::new, MobCategory.MONSTER, 0.6f, 1.95f,
            WorshipperEntity::createAttributes);

    public static final EntityType<DeerEntity> DEER = registerMob("deer",
            DeerEntity::new, MobCategory.CREATURE, 0.9f, 1.4f,
            DeerEntity::createAttributes);

    public static final EntityType<SpearEntity> SPEAR = registerProjectile("spear", SpearEntity::new);
    public static final EntityType<DartEntity> DART = registerProjectile("dart", DartEntity::new);
    public static final EntityType<MusketBallEntity> MUSKET_BALL = registerProjectile("musket_ball", MusketBallEntity::new);

    private ModEntities() {
    }

    private static <T extends Mob> EntityType<T> registerMob(String name, EntityType.EntityFactory<T> factory,
                                                             MobCategory category, float width, float height,
                                                             Supplier<AttributeSupplier.Builder> attributes) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, LifeModIds.id(name));
        UnaryOperator<FabricEntityType.Builder.Mob<T>> mobBuilder = mob -> mob
                .defaultAttributes(attributes)
                .spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        Mob::checkMobSpawnRules); // VERIFY-MAPPING: Mob.checkMobSpawnRules
        EntityType<T> type = FabricEntityType.Builder.createMob(factory, category, mobBuilder)
                .sized(width, height)
                .clientTrackingRange(10)
                .build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }

    private static <T extends Entity> EntityType<T> registerProjectile(String name, EntityType.EntityFactory<T> factory) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, LifeModIds.id(name));
        EntityType<T> type = EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(4)
                .build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }

    public static void init() {
        // Static initialisation registers the entity types.
    }
}
