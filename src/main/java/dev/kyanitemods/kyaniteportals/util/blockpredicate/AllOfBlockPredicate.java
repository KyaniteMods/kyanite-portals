package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public class AllOfBlockPredicate extends CombiningPredicate {
    public static final /*? if <1.20.5 {*//*Codec<AllOfBlockPredicate>*//*? } else { */MapCodec<AllOfBlockPredicate>/*? }*/ CODEC = codec(AllOfBlockPredicate::new);

    public AllOfBlockPredicate(List<BlockPredicate> values) {
        super(values);
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        for (BlockPredicate blockPredicate : predicates) {
            if (blockPredicate.test(level, blockPos)) continue;
            return false;
        }
        return true;
    }

    public BlockPredicateType<?> getType() {
        return BlockPredicateType.ALL_OF;
    }
}
