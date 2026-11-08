package io.github.emberbocor.ignoredworkstations.mark;

import io.github.emberbocor.ignoredworkstations.IgnoredWorkstations;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

final class MarkData extends SavedData {
    private final LongSet positions;

    private MarkData() {
        this(new LongOpenHashSet());
    }

    private MarkData(LongSet positions) {
        this.positions = positions;
    }

    static MarkData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(MarkData::load, MarkData::new, IgnoredWorkstations.MODID);
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
    public CompoundTag save(CompoundTag tag) {
        tag.putLongArray("positions", positions.toLongArray());
        return tag;
    }

    private static MarkData load(CompoundTag tag) {
        return new MarkData(new LongOpenHashSet(tag.getLongArray("positions")));
    }
}
