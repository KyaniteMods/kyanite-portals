package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SimpleBlockPredicate implements BlockPredicate {
    public static final SimpleBlockPredicate ANY = new SimpleBlockPredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    //$ map_codec_swap SimpleBlockPredicate
    public static final MapCodec<SimpleBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
        return new SimpleBlockPredicate(tag, blocks, state, nbt);
    }));

    private final Optional<TagKey<Block>> tag;
    private final Optional<Set<Block>> blocks;
    private final Optional<StatePropertiesPredicate> properties;
    private final Optional<NbtPredicate> nbt;

    public SimpleBlockPredicate(Optional<TagKey<Block>> tagKey, Optional<Set<Block>> set, Optional<StatePropertiesPredicate> statePropertiesPredicate, Optional<NbtPredicate> nbtPredicate) {
        this.tag = tagKey;
        this.blocks = set;
        this.properties = statePropertiesPredicate;
        this.nbt = nbtPredicate;
    }

    public boolean matches(BlockState state, @Nullable CompoundTag compoundTag) {
        if (tag.isPresent() && !state.is(tag.get())) {
            return false;
        } else if (blocks.isPresent() && !blocks.get().contains(state.getBlock())) {
            return false;
        } else if (properties.isPresent() && !properties.get().matches(state)) {
            return false;
        } else {
            return nbt.map(predicate -> predicate.matches(compoundTag)).orElse(true);
        }
    }

    @Override
    public boolean test(LevelReader level, BlockPos blockPos) {
        if (this == ANY) {
            return true;
        } else if (!level.hasChunkAt(blockPos)) {
            return false;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            return matches(level.getBlockState(blockPos), blockEntity == null ? null : blockEntity.saveWithFullMetadata(/*? if >=1.21 {*/level.registryAccess()/*? }*/));
        }
    }

    public Builder asBuilder() {
        return Builder.block().of(blocks.orElse(null)).of(tag.orElse(null)).setProperties(properties.orElse(null)).of(nbt.orElse(null));
    }

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

        public Builder add(Block... blocks) {
            if (this.blocks.isEmpty()) {
                return of(blocks);
            } else {
                ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
                builder.addAll(this.blocks.get());
                builder.add(blocks);
                this.blocks = Optional.of(builder.build());
            }
            return this;
        }

        public Builder add(Iterable<Block> iterable) {
            if (iterable == null) {
                return this;
            }
            if (this.blocks.isEmpty()) {
                return of(iterable);
            } else {
                ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
                builder.addAll(this.blocks.get());
                builder.addAll(iterable);
                this.blocks = Optional.of(builder.build());
            }
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

        public Builder of(NbtPredicate predicate) {
            this.nbt = predicate == null ? Optional.empty() : Optional.of(predicate);
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate statePropertiesPredicate) {
            this.properties = Optional.ofNullable(statePropertiesPredicate);
            return this;
        }

        public SimpleBlockPredicate build() {
            return new SimpleBlockPredicate(tag, blocks, properties, nbt);
        }
    }

    @Override
    public BlockPredicateType getType() {
        return BlockPredicateType.SIMPLE;
    }
}
