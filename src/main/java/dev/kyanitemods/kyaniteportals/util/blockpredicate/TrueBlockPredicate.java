package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;

public class TrueBlockPredicate implements BlockPredicate {
    public static final TrueBlockPredicate INSTANCE = new TrueBlockPredicate();

    public static final /*? if <1.20.5 {*/ /*Codec<TrueBlockPredicate>*//*? } else { */MapCodec<TrueBlockPredicate>/*? }*/ CODEC = /*? if <1.20.5 {*//*Codec*//*? } else { */MapCodec/*? }*/.unit(INSTANCE);

    private TrueBlockPredicate() {
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        return true;
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.TRUE;
    }
}
