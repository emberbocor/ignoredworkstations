package io.github.emberbocor.ignoredworkstations.mark;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

public final class WorkstationMarks {
    private static final Set<ResourceKey<PoiType>> JOB_SITES = Set.of(
            PoiTypes.ARMORER, PoiTypes.BUTCHER, PoiTypes.CARTOGRAPHER, PoiTypes.CLERIC, PoiTypes.FARMER,
            PoiTypes.FISHERMAN, PoiTypes.FLETCHER, PoiTypes.LEATHERWORKER, PoiTypes.LIBRARIAN, PoiTypes.MASON,
            PoiTypes.SHEPHERD, PoiTypes.TOOLSMITH, PoiTypes.WEAPONSMITH);

    private WorkstationMarks() {
    }

    /**
     * Marks or unmarks the clicked workstation.
     *
     * @return whether the click was used and vanilla must not handle it
     */
    public static boolean use(ServerLevel level, ServerPlayer player, ItemStack stack, InteractionHand hand, BlockPos pos) {
        // Waxing is not allowed in Adventure mode either (ItemStack#useOn).
        if (!player.getAbilities().mayBuild || jobSite(level.getBlockState(pos)) == null) {
            return false;
        }
        MarkData marks = MarkData.get(level);
        boolean marked = marks.contains(pos.asLong());
        if (!marked && stack.is(Items.HONEYCOMB)) {
            marks.add(pos.asLong());
            stack.consume(1, player);
            releaseClaims(level, pos);
            // A null player sends the event to the clicking player too; a vanilla client does not predict this click.
            level.levelEvent(null, LevelEvent.PARTICLES_WAX_ON, pos, 0);
            level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.sendOverlayMessage(Component.literal("Villagers will ignore this block"));
            return true;
        }
        if (marked && isAxe(stack) && !player.isSecondaryUseActive()) {
            marks.remove(pos.asLong());
            // A marked block has no valid claim; a villager that claimed it while the mod was removed leaves its ticket behind.
            releaseTicket(level, pos);
            stack.hurtAndBreak(1, player, hand);
            level.playSound(null, pos, SoundEvents.AXE_WAX_OFF.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(null, LevelEvent.PARTICLES_WAX_OFF, pos, 0);
            player.sendOverlayMessage(Component.literal("Villagers can use this block again"));
            return true;
        }
        return false;
    }

    public static void blockChanged(ServerLevel level, BlockPos pos, @Nullable BlockState oldState, BlockState newState) {
        if (oldState != null && !Objects.equals(jobSite(oldState), jobSite(newState))) {
            MarkData.get(level).remove(pos.asLong());
        }
    }

    public static boolean isMarked(ServerLevel level, BlockPos pos) {
        return MarkData.get(level).contains(pos.asLong());
    }

    public static Predicate<BlockPos> withoutMarked(ServerLevel level, Predicate<BlockPos> positions) {
        MarkData marks = MarkData.get(level);
        return marks.isEmpty() ? positions : pos -> !marks.contains(pos.asLong()) && positions.test(pos);
    }

    // Item.Properties#axe sets this component; it replaced AxeItem in 26.3.
    private static boolean isAxe(ItemStack stack) {
        Holder<BlockTransformer> transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
        return transformer != null && transformer.is(BlockTransformers.AXE);
    }

    private static void releaseClaims(ServerLevel level, BlockPos pos) {
        GlobalPos site = GlobalPos.of(level.dimension(), pos);
        for (ServerLevel other : level.getServer().getAllLevels()) {
            for (Villager villager : other.getEntities(EntityTypes.VILLAGER, villager -> true)) {
                forget(villager, MemoryModuleType.JOB_SITE, site);
                forget(villager, MemoryModuleType.POTENTIAL_JOB_SITE, site);
            }
        }
        releaseTicket(level, pos);
    }

    private static void releaseTicket(ServerLevel level, BlockPos pos) {
        PoiManager poiManager = level.getPoiManager();
        if (poiManager.getType(pos).isPresent()) {
            poiManager.release(pos);
        }
    }

    private static void forget(Villager villager, MemoryModuleType<GlobalPos> memory, GlobalPos site) {
        if (villager.getBrain().getMemory(memory).filter(site::equals).isPresent()) {
            villager.getBrain().eraseMemory(memory);
        }
    }

    @Nullable
    private static ResourceKey<PoiType> jobSite(BlockState state) {
        return PoiTypes.forState(state).flatMap(Holder::unwrapKey).filter(JOB_SITES::contains).orElse(null);
    }
}
