package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.blockpredicates.HasSturdyFacePredicate;

public class HasSturdyFaceBlockPredicate implements BlockPredicate {
    //$ map_codec_swap HasSturdyFaceBlockPredicate
    public static final MapCodec<HasSturdyFaceBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(hasSturdyFacePredicate -> hasSturdyFacePredicate.offset),
            Direction.CODEC.fieldOf("direction").forGetter(hasSturdyFacePredicate -> hasSturdyFacePredicate.direction)
    ).apply(instance, HasSturdyFaceBlockPredicate::new));

    private final Vec3i offset;
    private final Direction direction;

    public HasSturdyFaceBlockPredicate(Vec3i vec3i, Direction direction) {
        this.offset = vec3i;
        this.direction = direction;
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.offset(offset);
        return level.getBlockState(blockPos2).isFaceSturdy(level, blockPos2, direction);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.HAS_STURDY_FACE;
    }
}
