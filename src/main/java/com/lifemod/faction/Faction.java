package com.lifemod.faction;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * All factions of the mod. Ported & extended from the Diversity 1.7.2 tribes.
 *
 * <ul>
 *   <li>GOOD factions never fight, not even in self-defence.</li>
 *   <li>NEUTRAL factions may declare wars (see {@link WarState}) and enslave others.</li>
 *   <li>EVIL factions are at war with everyone by default but never attack players unprovoked.</li>
 * </ul>
 */
public enum Faction {
    VILLAGERS(Alignment.GOOD, "chief",
            List.of("villager", "farmer", "butcher", "librarian", "priest", "smith", "guard", "innkeeper"),
            List.of(ConventionalBiomeTags.IS_PLAINS, ConventionalBiomeTags.IS_FOREST), false),
    EGYPTIANS(Alignment.GOOD, "pharaoh",
            List.of("farmer", "sculptor", "scribe", "priest", "painter", "guard"),
            List.of(ConventionalBiomeTags.IS_DESERT), false),
    ELVES(Alignment.NEUTRAL, "owner",
            List.of("elf", "amazon", "trader", "healer"),
            List.of(ConventionalBiomeTags.IS_JUNGLE), false),
    NORTHERNERS(Alignment.GOOD, "chief",
            List.of("villager", "hunter", "reindeer_herder"),
            List.of(ConventionalBiomeTags.IS_SNOWY), false),
    AZTECS(Alignment.EVIL, "chief",
            List.of("farmer", "hunter", "dyer", "highpriest", "breeder"),
            List.of(ConventionalBiomeTags.IS_JUNGLE), false),
    NORDS(Alignment.NEUTRAL, "chief",
            List.of("villager", "warrior", "hunter"),
            List.of(ConventionalBiomeTags.IS_SNOWY), false),
    VEGIRS(Alignment.NEUTRAL, "chief",
            List.of("villager", "hunter", "trapper"),
            List.of(ConventionalBiomeTags.IS_TAIGA, ConventionalBiomeTags.IS_SNOWY), false),
    BANDITS(Alignment.EVIL, "ataman",
            List.of("bandit", "shieldbearer", "twisted_villager"),
            List.of(), true),
    WILDLINGS(Alignment.EVIL, "chief",
            List.of("villager", "warrior"),
            List.of(), true),
    MERCENARIES(Alignment.NEUTRAL, "captain",
            List.of("mercenary", "recruiter"),
            List.of(), true),
    PIRATES(Alignment.EVIL, "captain",
            List.of("pirate", "officer", "musketeer", "cook", "slave"),
            List.of(ConventionalBiomeTags.IS_BEACH, ConventionalBiomeTags.IS_OCEAN), false),
    DWARVES(Alignment.NEUTRAL, "king",
            List.of("miner", "smith", "trader"),
            List.of(ConventionalBiomeTags.IS_UNDERGROUND, ConventionalBiomeTags.IS_CAVE), false),
    GOBLINS(Alignment.EVIL, "chief",
            List.of("goblin"),
            List.of(ConventionalBiomeTags.IS_UNDERGROUND, ConventionalBiomeTags.IS_CAVE), false);

    public static final Codec<Faction> CODEC = Codec.STRING.xmap(Faction::byName, Faction::getSerializedName);

    private final Alignment alignment;
    private final String leaderProfession;
    private final List<String> professions;
    private final List<TagKey<Biome>> homeBiomes;
    private final boolean biomeSkins;

    Faction(Alignment alignment, String leaderProfession, List<String> professions,
            List<TagKey<Biome>> homeBiomes, boolean biomeSkins) {
        this.alignment = alignment;
        this.leaderProfession = leaderProfession;
        this.professions = professions;
        this.homeBiomes = homeBiomes;
        this.biomeSkins = biomeSkins;
    }

    public Alignment alignment() {
        return this.alignment;
    }

    public String leaderProfession() {
        return this.leaderProfession;
    }

    public List<String> professions() {
        return this.professions;
    }

    /** Biome tags this faction settles in; empty list means "anywhere". */
    public List<TagKey<Biome>> homeBiomes() {
        return this.homeBiomes;
    }

    /** Whether members of this faction pick a skin variant based on the biome they spawned in. */
    public boolean hasBiomeSkins() {
        return this.biomeSkins;
    }

    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String translationKey() {
        return "faction." + com.lifemod.LifeModIds.MOD_ID + "." + this.getSerializedName();
    }

    public static Faction byName(String name) {
        try {
            return Faction.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return VILLAGERS;
        }
    }

    /** True when the given biome belongs to this faction's home biomes (or the faction lives anywhere). */
    public boolean likesBiome(Holder<Biome> biome) {
        if (this.homeBiomes.isEmpty()) {
            return true;
        }

        for (TagKey<Biome> tag : this.homeBiomes) {
            if (biome.is(tag)) {
                return true;
            }
        }

        return false;
    }

    /** Picks a faction for a member spawning in the given biome; biome-bound factions are preferred over the "anywhere" ones. */
    public static Faction pickForBiome(Holder<Biome> biome, RandomSource random) {
        List<Faction> bound = new ArrayList<>();
        List<Faction> anywhere = new ArrayList<>();

        for (Faction faction : values()) {
            if (faction.homeBiomes.isEmpty()) {
                anywhere.add(faction);
            } else if (faction.likesBiome(biome)) {
                bound.add(faction);
            }
        }

        // Roughly 2/3 chance to pick a faction that actually calls this biome home.
        if (!bound.isEmpty() && (anywhere.isEmpty() || random.nextInt(3) != 0)) {
            return bound.get(random.nextInt(bound.size()));
        }

        if (!anywhere.isEmpty()) {
            return anywhere.get(random.nextInt(anywhere.size()));
        }

        return VILLAGERS;
    }

    /** Skin variant for factions whose look depends on the biome (bandits, wildlings, mercenaries). */
    public static String skinForBiome(Holder<Biome> biome) {
        if (biome.is(ConventionalBiomeTags.IS_SNOWY) || biome.is(ConventionalBiomeTags.IS_ICY)) {
            return "snow";
        }

        if (biome.is(ConventionalBiomeTags.IS_DESERT)) {
            return "desert";
        }

        if (biome.is(ConventionalBiomeTags.IS_JUNGLE)) {
            return "jungle";
        }

        if (biome.is(ConventionalBiomeTags.IS_TAIGA)) {
            return "taiga";
        }

        return "plains";
    }
}
