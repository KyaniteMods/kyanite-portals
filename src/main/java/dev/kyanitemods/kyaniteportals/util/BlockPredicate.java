package dev.kyanitemods.kyaniteportals.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BlockPredicate {
    public static final BlockPredicate ANY = new BlockPredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    private final Optional<TagKey<Block>> tag;
    private final Optional<Set<Block>> blocks;
    private final Optional<StatePropertiesPredicate> properties;
    private final Optional<NbtPredicate> nbt;

    public BlockPredicate(Optional<TagKey<Block>> tagKey, Optional<Set<Block>> set, Optional<StatePropertiesPredicate> statePropertiesPredicate, Optional<NbtPredicate> nbtPredicate) {
        this.tag = tagKey;
        this.blocks = set;
        this.properties = statePropertiesPredicate;
        this.nbt = nbtPredicate;
    }

    public boolean matches(LevelReader level, BlockPos blockPos) {
        if (this == ANY) {
            return true;
        } else if (!level.hasChunkAt(blockPos)) {
            return false;
        } else {
            BlockState blockState = level.getBlockState(blockPos);
            if (tag.isPresent() && !blockState.is(tag.get())) {
                return false;
            } else if (blocks.isPresent() && !blocks.get().contains(blockState.getBlock())) {
                return false;
            } else if (properties.isPresent() && !properties.get().matches(blockState)) {
                return false;
            } else {
                if (nbt.isPresent()) {
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    if (blockEntity == null || !nbt.get().matches(blockEntity.saveWithFullMetadata(/*? if >=1.21 {*/level.registryAccess()/*? }*/))) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.BLOCK)
                    .optionalFieldOf("tag")
                    .forGetter(predicate -> predicate.tag),
            BuiltInRegistries.BLOCK.byNameCodec()
                    .listOf()
                    .xmap(Set::copyOf, List::copyOf)
                    .optionalFieldOf("blocks")
                    .forGetter(predicate -> predicate.blocks),
            CodecHelper.STATE_PROPERTIES_PREDICATE_CODEC
                    .optionalFieldOf("state")
                    .forGetter(predicate -> predicate.properties),
            CodecHelper.NBT_PREDICATE_CODEC
                    .optionalFieldOf("nbt")
                    .forGetter(predicate -> predicate.nbt)
    ).apply(instance, (tag, blocks, state, nbt) -> {
        if (tag.isEmpty() && blocks.isEmpty() && state.isEmpty() && nbt.isEmpty()) return ANY;
        return new BlockPredicate(tag, blocks, state, nbt);
    }));

    public static class Builder {
        private Optional<Set<Block>> blocks = Optional.empty();
        private Optional<TagKey<Block>> tag = Optional.empty();
        private Optional<StatePropertiesPredicate> properties = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();

        private Builder() {
        }

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block... blocks) {
            this.blocks = Optional.of(ImmutableSet.copyOf(blocks));
            return this;
        }

        public Builder of(Iterable<Block> iterable) {
            this.blocks = iterable == null ? Optional.empty() : Optional.of(ImmutableSet.copyOf(iterable));
            return this;
        }

        public Builder of(TagKey<Block> tagKey) {
            this.tag = Optional.ofNullable(tagKey);
            return this;
        }

        public Builder hasNbt(CompoundTag compoundTag) {
            this.nbt = compoundTag == null ? Optional.empty() : Optional.of(new NbtPredicate(compoundTag));
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate statePropertiesPredicate) {
            this.properties = Optional.ofNullable(statePropertiesPredicate);
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(tag, blocks, properties, nbt);
        }
    }
}
