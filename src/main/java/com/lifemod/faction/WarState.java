package com.lifemod.faction;

import com.lifemod.LifeModIds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * World-wide war declarations between factions. Stored in the overworld's data storage.
 * EVIL factions do not need entries here: they are hostile to everyone by default
 * (see {@link FactionRelations}).
 */
public class WarState extends SavedData {
    public record WarEntry(Faction a, Faction b) {
        public static final Codec<WarEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Faction.CODEC.fieldOf("a").forGetter(WarEntry::a),
                Faction.CODEC.fieldOf("b").forGetter(WarEntry::b)
        ).apply(instance, WarEntry::new));

        public boolean matches(Faction x, Faction y) {
            return (this.a == x && this.b == y) || (this.a == y && this.b == x);
        }
    }

    public static final Codec<WarState> CODEC = WarEntry.CODEC.listOf()
            .xmap(WarState::new, state -> List.copyOf(state.wars));

    public static final SavedDataType<WarState> TYPE =
            new SavedDataType<>(LifeModIds.id("war_state"), WarState::new, CODEC, null);

    private final List<WarEntry> wars;

    public WarState() {
        this.wars = new ArrayList<>();
    }

    private WarState(List<WarEntry> wars) {
        this.wars = new ArrayList<>(wars);
    }

    public static WarState get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean atWar(Faction a, Faction b) {
        if (a == b) {
            return false;
        }

        for (WarEntry war : this.wars) {
            if (war.matches(a, b)) {
                return true;
            }
        }

        return false;
    }

    /** Declares war between two factions. Only NEUTRAL factions may initiate wars. */
    public boolean declareWar(Faction initiator, Faction target) {
        if (initiator == target || initiator.alignment() != Alignment.NEUTRAL) {
            return false;
        }

        if (this.atWar(initiator, target)) {
            return false;
        }

        this.wars.add(new WarEntry(initiator, target));
        this.setDirty();
        return true;
    }

    public boolean makePeace(Faction a, Faction b) {
        boolean removed = this.wars.removeIf(war -> war.matches(a, b));

        if (removed) {
            this.setDirty();
        }

        return removed;
    }

    public List<WarEntry> wars() {
        return List.copyOf(this.wars);
    }
}
