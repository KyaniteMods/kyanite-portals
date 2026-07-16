package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockChangeTrigger extends SimplePortalTrigger<BlockChangeTrigger.BlockChangeTriggerInstance> {
    @Override
    public /*? if <1.20.6 {*//*Codec<BlockChangeTriggerInstance>*//*? } else {*/MapCodec<BlockChangeTriggerInstance>/*? }*/ codec() {
        return BlockChangeTriggerInstance.CODEC;
    }

    public TriggerResult trigger(ServerLevel level, BlockPos pos, @Nullable Player player) {
        return trigger(level, pos, player, instance -> BlockChangeTriggerInstance.POSITIONS, (instance, triggerPos) -> instance.matches(level, triggerPos), (instance, triggerPos) -> instance.beforeTrigger(level, triggerPos, player), (instance, triggerPos, result) -> instance.onTrigger(result, level, triggerPos, player));
    }

    public BlockChangeTriggerInstance create(BlockPredicate predicate) {
        return new BlockChangeTriggerInstance(predicate);
    }

    public static class BlockChangeTriggerInstance extends AbstractPortalTriggerInstance<BlockChangeTriggerInstance> {
        public static final List<Vec3i> POSITIONS = List.of(Vec3i.ZERO);

        //$ map_codec_swap BlockChangeTriggerInstance
        public static final MapCodec<BlockChangeTriggerInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockPredicate.CODEC.fieldOf("predicate").forGetter(i -> i.predicate)
        ).apply(instance, BlockChangeTriggerInstance::new));

        private final BlockPredicate predicate;

        public BlockChangeTriggerInstance(BlockPredicate predicate) {
            super(PortalTriggers.BLOCK_CHANGE);
            this.predicate = predicate;
        }

        public boolean matches(WorldGenLevel level, BlockPos pos) {
            return predicate.test(level, pos);
        }

        public void onTrigger(TriggerResult result, WorldGenLevel level, BlockPos pos, @Nullable Player player) {
        }

        public void beforeTrigger(WorldGenLevel level, BlockPos pos, @Nullable Player player) {
        }
    }
}
