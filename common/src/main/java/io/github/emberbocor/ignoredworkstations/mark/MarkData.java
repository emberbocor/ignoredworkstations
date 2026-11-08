package io.github.emberbocor.ignoredworkstations.mark;

import io.github.emberbocor.ignoredworkstations.IgnoredWorkstations;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

final class MarkData extends SavedData {
    // Vanilla needs a data fix type; this one has no fixes for data saved by the running version.
    private static final SavedData.Factory<MarkData> FACTORY =
            new SavedData.Factory<>(MarkData::new, MarkData::load, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final LongSet positions;

    private MarkData() {
        this(new LongOpenHashSet());
    }

    private MarkData(LongSet positions) {
        this.positions = positions;
    }

    static MarkData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, IgnoredWorkstations.MODID);
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

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray("positions", positions.toLongArray());
        return tag;
    }

    private static MarkData load(CompoundTag tag, HolderLookup.Provider registries) {
        return new MarkData(new LongOpenHashSet(tag.getLongArray("positions")));
    }
}
