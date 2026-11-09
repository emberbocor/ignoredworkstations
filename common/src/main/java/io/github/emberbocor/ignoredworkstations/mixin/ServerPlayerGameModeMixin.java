package io.github.emberbocor.ignoredworkstations.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.ignoredworkstations.mark.WorkstationMarks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeMixin {
    @Unique
    @Nullable
    private BlockPos ignoredworkstations$usedPos;
    @Unique
    private int ignoredworkstations$usedTick;

    // Runs after the spectator branch and the NeoForge / Forge right click event, before the block or the item act.
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSecondaryUseActive()Z"), cancellable = true)
    private void ignoredworkstations$useWorkstation(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = hitResult.getBlockPos();
        int tick = serverLevel.getServer().getTickCount();
        // A vanilla client that did not predict the click also sends the off hand, in the same or the next server tick.
        if (hand == InteractionHand.OFF_HAND && pos.equals(ignoredworkstations$usedPos) && tick - ignoredworkstations$usedTick <= 1) {
            player.inventoryMenu.sendAllDataToRemote();
            cir.setReturnValue(InteractionResult.FAIL);
        } else if (WorkstationMarks.use(serverLevel, player, stack, hand, pos)) {
            ignoredworkstations$usedPos = pos;
            ignoredworkstations$usedTick = tick;
            // A vanilla client does not predict this click, so the server has to swing the arm.
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
        }
    }
}
