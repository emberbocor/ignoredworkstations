package io.github.emberbocor.ignoredworkstations.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.SectionStorage;

@Mixin(SectionStorage.class)
interface SectionStorageAccessor {
    @Accessor
    LevelHeightAccessor getLevelHeightAccessor();
}
