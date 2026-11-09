package io.github.emberbocor.ignoredworkstations.mark;

import java.util.stream.LongStream;

import com.mojang.serialization.Codec;

import io.github.emberbocor.ignoredworkstations.IgnoredWorkstations;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

final class MarkData extends SavedData {
    private static final Codec<MarkData> CODEC = Codec.LONG_STREAM.fieldOf("positions").codec()
            .xmap(positions -> new MarkData(new LongOpenHashSet(positions.toArray())), data -> LongStream.of(data.positions.toLongArray()));
    // Vanilla needs a data fix type; this one has no fixes for 1.21.1 or newer data.
    private static final SavedDataType<MarkData> TYPE =
            new SavedDataType<>(IgnoredWorkstations.MODID, MarkData::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final LongSet positions;

    private MarkData() {
        this(new LongOpenHashSet());
    }

    private MarkData(LongSet positions) {
        this.positions = positions;
    }

    static MarkData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    boolean isEmpty() {
        return positions.isEmpty();
    }

    boolean contains(long pos) {
        return positions.contains(pos);
    }

    void add(long pos) {
        if (positions.add(pos)) {
            setDirty();
        }
    }

    void remove(long pos) {
        if (positions.remove(pos)) {
            setDirty();
        }
    }
}
