package io.github.emberbocor.ignoredworkstations.mark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.LongStream;

import com.mojang.serialization.Codec;

import io.github.emberbocor.ignoredworkstations.IgnoredWorkstations;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;

final class MarkData extends SavedData {
    private static final Codec<MarkData> CODEC = Codec.LONG_STREAM.fieldOf("positions").codec()
            .xmap(positions -> new MarkData(new LongOpenHashSet(positions.toArray())), data -> LongStream.of(data.positions.toLongArray()));
    // Vanilla needs a data fix type; this one has no fixes for 1.21.1 or newer data.
    private static final SavedDataType<MarkData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(IgnoredWorkstations.MODID, "marks"), MarkData::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final LongSet positions;

    private MarkData() {
        this(new LongOpenHashSet());
    }

    private MarkData(LongSet positions) {
        this.positions = positions;
    }

    static MarkData get(ServerLevel level) {
        SavedDataStorage storage = level.getDataStorage();
        MarkData data = storage.get(TYPE);
        if (data == null) {
            data = readLegacy(level);
            // set() marks the data dirty: the next save writes the new file, and the old one is not read again.
            storage.set(TYPE, data);
        }
        return data;
    }

    /** Marks of a world from before 26.1: vanilla leaves that file in the old data folder of the dimension. */
    private static MarkData readLegacy(ServerLevel level) {
        Path file = legacyFolder(level).resolve("data").resolve(IgnoredWorkstations.MODID + ".dat");
        if (Files.exists(file)) {
            try {
                return CODEC.parse(NbtOps.INSTANCE, NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()).get("data")).getOrThrow();
            } catch (Exception e) {
                IgnoredWorkstations.LOGGER.error("Could not read the marks in {}", file, e);
            }
        }
        return new MarkData();
    }

    private static Path legacyFolder(ServerLevel level) {
        Path root = level.getServer().getWorldPath(LevelResource.ROOT);
        ResourceKey<Level> dimension = level.dimension();
        if (dimension == Level.OVERWORLD) {
            return root;
        }
        if (dimension == Level.NETHER) {
            return root.resolve("DIM-1");
        }
        if (dimension == Level.END) {
            return root.resolve("DIM1");
        }
        return dimension.identifier().resolveAgainst(root.resolve("dimensions"));
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
