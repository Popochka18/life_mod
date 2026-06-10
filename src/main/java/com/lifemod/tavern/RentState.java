package com.lifemod.tavern;

import com.lifemod.LifeModIds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Per-level registry of rented (tavern rooms) and owned (bought houses) doors.
 * An expiry of {@code PERMANENT} marks a door bought with a house deed.
 */
public class RentState extends SavedData {
    public static final long PERMANENT = -1L;

    public record Rental(long pos, String renter, long expiry) {
        public static final Codec<Rental> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("pos").forGetter(Rental::pos),
                Codec.STRING.fieldOf("renter").forGetter(Rental::renter),
                Codec.LONG.fieldOf("expiry").forGetter(Rental::expiry)
        ).apply(instance, Rental::new));

        public boolean active(long gameTime) {
            return this.expiry == PERMANENT || gameTime < this.expiry;
        }
    }

    public static final Codec<RentState> CODEC = Rental.CODEC.listOf()
            .xmap(RentState::new, state -> List.copyOf(state.rentals.values()));

    public static final SavedDataType<RentState> TYPE =
            new SavedDataType<>(LifeModIds.id("rent_state"), RentState::new, CODEC, null);

    private final Map<Long, Rental> rentals = new HashMap<>();

    public RentState() {
    }

    private RentState(List<Rental> entries) {
        for (Rental rental : entries) {
            this.rentals.put(rental.pos(), rental);
        }
    }

    public static RentState get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    /** Whether the given player may open the door at {@code pos}; true when the door is free or rented by them. */
    public boolean mayUse(BlockPos pos, UUID player, long gameTime) {
        Rental rental = this.rentals.get(pos.asLong());

        if (rental == null || !rental.active(gameTime)) {
            return true;
        }

        return rental.renter().equals(player.toString());
    }

    public boolean isFree(BlockPos pos, long gameTime) {
        Rental rental = this.rentals.get(pos.asLong());
        return rental == null || !rental.active(gameTime);
    }

    public void rent(BlockPos pos, UUID player, long expiry) {
        this.rentals.put(pos.asLong(), new Rental(pos.asLong(), player.toString(), expiry));
        this.setDirty();
    }

    public void release(BlockPos pos) {
        if (this.rentals.remove(pos.asLong()) != null) {
            this.setDirty();
        }
    }
}
