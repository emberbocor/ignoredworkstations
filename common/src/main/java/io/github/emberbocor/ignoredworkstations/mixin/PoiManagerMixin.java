package io.github.emberbocor.ignoredworkstations.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.ignoredworkstations.mark.WorkstationMarks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

@Mixin(PoiManager.class)
abstract class PoiManagerMixin {
    // AcquirePoi only considers the closest five results, so marked sites must be gone before that limit.
    @ModifyVariable(method = "findAllClosestFirstWithType", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private Predicate<BlockPos> ignoredworkstations$withoutMarked(Predicate<BlockPos> positions) {
        return ((SectionStorageAccessor) (Object) this).getLevelHeightAccessor() instanceof ServerLevel level
                ? WorkstationMarks.withoutMarked(level, positions)
                : positions;
    }

    // Lithium replaces the search of AcquirePoi with this method of its own; it is absent without Lithium.
    @ModifyVariable(method = "lithium$getNClosestFirstWithType", at = @At("HEAD"), argsOnly = true, ordinal = 1, remap = false, require = 0)
    private Predicate<BlockPos> ignoredworkstations$lithiumWithoutMarked(Predicate<BlockPos> positions) {
        return ignoredworkstations$withoutMarked(positions);
    }

    // ValidateNearbyPoi: a villager loaded from disk may still remember a site that was marked while it was unloaded.
    @Inject(method = "exists", at = @At("HEAD"), cancellable = true)
    private void ignoredworkstations$markedDoesNotExist(BlockPos pos, Predicate<Holder<PoiType>> typePredicate, CallbackInfoReturnable<Boolean> cir) {
        if (((SectionStorageAccessor) (Object) this).getLevelHeightAccessor() instanceof ServerLevel level && WorkstationMarks.isMarked(level, pos)) {
            cir.setReturnValue(false);
        }
    }
}
