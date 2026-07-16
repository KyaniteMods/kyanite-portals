package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import java.util.function.BiPredicate;

public interface BlockPredicate extends BiPredicate<LevelReader, BlockPos> {
    Codec<BlockPredicate> CODEC = BlockPredicateType.REGISTRY.byNameCodec().dispatch(BlockPredicate::getType, BlockPredicateType::codec);

    BlockPredicateType<?> getType();
}
