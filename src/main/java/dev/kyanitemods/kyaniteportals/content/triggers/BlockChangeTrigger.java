package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Experimental
public class BlockChangeTrigger extends SimplePortalTrigger<BlockChangeTrigger.BlockChangeTriggerInstance> {
    @Override
    public /*? if <1.20.6 {*//*Codec<BlockChangeTriggerInstance>*//*? } else {*/MapCodec<BlockChangeTriggerInstance>/*? }*/ codec() {
        return BlockChangeTriggerInstance.CODEC;
    }

    public TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, BlockState state) {
        return trigger(level, pos, player, instance -> BlockChangeTriggerInstance.POSITIONS, (instance, triggerPos) -> instance.matches(state), (instance, triggerPos) -> instance.beforeTrigger(level, triggerPos, player), (instance, triggerPos, result) -> instance.onTrigger(result, level, triggerPos, player));
    }

    public BlockChangeTriggerInstance create(BlockState state) {
        return new BlockChangeTriggerInstance(state);
    }

    public static class BlockChangeTriggerInstance extends AbstractPortalTriggerInstance<BlockChangeTriggerInstance> {
        public static final List<Vec3i> POSITIONS = List.of(Vec3i.ZERO);

        //$ map_codec_swap BlockChangeTriggerInstance
        public static final MapCodec<BlockChangeTriggerInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockState.CODEC.fieldOf("block").forGetter(i -> i.blockState)
        ).apply(instance, BlockChangeTriggerInstance::new));

        private final BlockState blockState;

        public BlockChangeTriggerInstance(BlockState blockState) {
            super(PortalTriggers.BLOCK_CHANGE);
            this.blockState = blockState;
        }

        public boolean matches(BlockState state) {
            return blockState.getBlock().equals(state.getBlock());
        }

        public void onTrigger(TriggerResult result, Level level, BlockPos pos, @Nullable Player player) {
        }

        public void beforeTrigger(Level level, BlockPos pos, @Nullable Player player) {
        }
    }
}
