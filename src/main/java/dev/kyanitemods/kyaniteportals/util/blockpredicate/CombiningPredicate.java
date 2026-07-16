package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.function.Function;

public abstract class CombiningPredicate implements BlockPredicate {
    protected final List<BlockPredicate> predicates;

    protected CombiningPredicate(List<BlockPredicate> predicates) {
        this.predicates = predicates;
    }

    public static <T extends CombiningPredicate> /*? if <1.20.5 {*//*Codec<T>*//*? } else { */MapCodec<T>/*? }*/ codec(Function<List<BlockPredicate>, T> function) {
        return RecordCodecBuilder./*? if <1.20.5 {*//*create*//*? } else { */mapCodec/*? }*/(instance -> instance.group(BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter(predicate -> predicate.predicates)).apply(instance, function));
    }
}
