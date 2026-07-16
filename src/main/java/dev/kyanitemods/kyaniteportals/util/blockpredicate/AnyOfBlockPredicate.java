package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public class AnyOfBlockPredicate extends CombiningPredicate {
    public static final /*? if <1.20.5 {*//*Codec<AnyOfBlockPredicate>*//*? } else { */MapCodec<AnyOfBlockPredicate>/*? }*/ CODEC = codec(AnyOfBlockPredicate::new);

    public AnyOfBlockPredicate(List<BlockPredicate> list) {
        super(list);
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        for (BlockPredicate predicate : predicates) {
            if (!predicate.test(level, blockPos)) continue;
            return true;
        }
        return false;
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.ANY_OF;
    }
}
