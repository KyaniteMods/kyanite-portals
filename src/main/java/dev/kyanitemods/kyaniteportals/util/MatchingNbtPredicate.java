package dev.kyanitemods.kyaniteportals.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class MatchingNbtPredicate implements BlockPredicate {
    //$ map_codec_swap MatchingNbtPredicate
    public static final MapCodec<MatchingNbtPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(predicate -> predicate.offset),
            CompoundTag.CODEC.fieldOf("nbt").forGetter(predicate -> predicate.nbt)
    ).apply(instance, MatchingNbtPredicate::new));

    public static final BlockPredicateType<MatchingNbtPredicate> TYPE;

    protected final Vec3i offset;
    protected final CompoundTag nbt;

    protected MatchingNbtPredicate(Vec3i offset, CompoundTag nbt) {
        this.offset = offset;
        this.nbt = nbt;
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && NbtUtils.compareNbt(blockEntity.saveWithFullMetadata(/*? if >=1.21 {*/level.registryAccess()/*? }*/), nbt, true);
    }

    public static MatchingNbtPredicate of(CompoundTag nbt) {
        return of(Vec3i.ZERO, nbt);
    }

    public static MatchingNbtPredicate of(Vec3i offset, CompoundTag nbt) {
        return new MatchingNbtPredicate(offset, nbt);
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }

    static {
        TYPE = Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, KyanitePortals.id("matching_nbt"), () -> CODEC);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Registering MatchingNbtPredicate");
    }
}
