package io.github.emberbocor.ignoredworkstations.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.ignoredworkstations.mark.WorkstationMarks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
    @Shadow
    @Final
    Level level;

    // Every block change on a loaded chunk ends here, whatever its update flags; the return value is the replaced state.
    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void ignoredworkstations$blockChanged(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof ServerLevel serverLevel) {
            WorkstationMarks.blockChanged(serverLevel, pos, cir.getReturnValue(), state);
        }
    }
}
