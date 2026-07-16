package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NotBlockPredicate implements BlockPredicate {
    //$ map_codec_swap NotBlockPredicate
    public static final MapCodec<NotBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(predicate -> predicate.predicate)
    ).apply(instance, NotBlockPredicate::new));

    private final BlockPredicate predicate;

    public NotBlockPredicate(BlockPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        return !predicate.test(level, blockPos);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.NOT;
    }
}
