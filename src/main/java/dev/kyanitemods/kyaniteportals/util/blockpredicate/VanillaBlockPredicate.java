package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;

public class VanillaBlockPredicate implements BlockPredicate {
    //$ map_codec_swap VanillaBlockPredicate
    public static final MapCodec<VanillaBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.CODEC.fieldOf("predicate").forGetter(predicate -> predicate.predicate)
    ).apply(instance, VanillaBlockPredicate::new));

    private final net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate predicate;

    public VanillaBlockPredicate(net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        return level instanceof WorldGenLevel worldGenLevel && predicate.test(worldGenLevel, blockPos);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.VANILLA;
    }
}
