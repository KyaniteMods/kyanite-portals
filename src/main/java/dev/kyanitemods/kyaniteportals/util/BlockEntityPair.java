package dev.kyanitemods.kyaniteportals.util;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
//? if >=1.21
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.6
import net.minecraft.world.level.storage.TagValueInput;

public record BlockEntityPair(BlockState state, CompoundTag nbt) {
    public static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.either(
            BuiltInRegistries.BLOCK.byNameCodec(),
            BlockState.CODEC
    ).xmap(either -> either.left().map(Block::defaultBlockState).orElseGet(either.right()::get), state -> {
        if (state.equals(state.getBlock().defaultBlockState())) return Either.left(state.getBlock());
        return Either.right(state);
    });

    public static final Codec<BlockEntityPair> PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BLOCK_STATE_CODEC.fieldOf("block").forGetter(BlockEntityPair::state),
            CodecHelper.FLATTENED_TAG_CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(BlockEntityPair::nbt)
    ).apply(instance, BlockEntityPair::new));

    public static final Codec<BlockEntityPair> CODEC = Codec.either(BLOCK_STATE_CODEC, PAIR_CODEC)
            .xmap(either -> either.left().map(state -> new BlockEntityPair(state, new CompoundTag())).orElseGet(either.right()::get), pair -> {
                if (pair.nbt().isEmpty()) return Either.left(pair.state());
                return Either.right(pair);
            });

    public void set(LevelAccessor level, BlockPos pos, int update, int updateLimit) {
        level.setBlock(pos, state(), update, updateLimit);
        setBlockEntity(level, pos);
    }

    public void set(LevelAccessor level, BlockPos pos, int update) {
        level.setBlock(pos, state(), update);
        setBlockEntity(level, pos);
    }

    public void setBlockEntity(LevelAccessor level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (nbt().isEmpty()) return;
        if (blockEntity != null) {
            //? if <1.21 {
            /*blockEntity.load(nbt());
            *///? } else if <1.21.6 {
            //blockEntity.loadWithComponents(nbt(), level.registryAccess());
            //? } else {
            try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(KyanitePortals.LOGGER);){
                blockEntity.loadWithComponents(TagValueInput.create(scopedCollector, level.registryAccess(), nbt()));
            }
            //? }
            blockEntity.setChanged();
        } else {
            KyanitePortals.LOGGER.error("Set " + this + " at " + pos + ", but block entity was null!");
        }
    }

    public BlockEntityPair with(BlockState state) {
        return new BlockEntityPair(state, nbt());
    }

    public BlockEntityPair with(CompoundTag nbt) {
        return new BlockEntityPair(state(), nbt);
    }

    @Override
    public String toString() {
        return "BlockEntityPair{" +
                "state=" + state +
                ", nbt=" + nbt +
                '}';
    }
}
