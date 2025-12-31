package dev.kyanitemods.kyaniteportals.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record DimensionList(Set<ResourceKey<LevelStem>> dimensions, Optional<TagKey<LevelStem>> tag) {
    public static final DimensionList EMPTY = new DimensionList(Set.of(), Optional.empty());

    public static final Codec<DimensionList> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.LEVEL_STEM).listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("dimensions", Set.of()).forGetter(DimensionList::dimensions),
            TagKey.codec(Registries.LEVEL_STEM).optionalFieldOf("tag").forGetter(DimensionList::tag)
    ).apply(instance, DimensionList::new));

    public static final Codec<DimensionList> HASHED_TAG_CODEC = TagKey.codec(Registries.LEVEL_STEM).flatXmap(tag -> DataResult.success(new DimensionList(Set.of(), Optional.of(tag))), (DimensionList list) -> {
        if (list.tag().isEmpty()) return DataResult.error(() -> "Tag is not present");
        return DataResult.success(list.tag().get());
    });

    public static final Codec<DimensionList> SET_CODEC = ResourceKey.codec(Registries.LEVEL_STEM).listOf()
            .xmap(list -> new DimensionList(Set.copyOf(list), Optional.empty()), list -> List.copyOf(list.dimensions()));

    public static final Codec<DimensionList> CODEC = Codec.either(
            Codec.either(FULL_CODEC, SET_CODEC)
                    .xmap(either -> either.left().orElseGet(either.right()::get),
                            list -> list.tag().isPresent() ? Either.left(list) : Either.right(list)),
                    HASHED_TAG_CODEC)
            .xmap(either -> either.left().orElseGet(either.right()::get),
                    list -> list.dimensions().isEmpty() ? Either.right(list) : Either.left(list));

    public boolean matches(Level level) {
        if (dimensions().contains(Registries.levelToLevelStem(level.dimension()))) return true;
        return !level.isClientSide() && tag().isPresent() && LevelHelper.asStemHolder(level).is(tag().get());
    }

    public List<ResourceKey<LevelStem>> get(HolderLookup.RegistryLookup<LevelStem> registry) {
        List<ResourceKey<LevelStem>> list = new ArrayList<>(dimensions());
        if (tag().isPresent())
            list.addAll(registry.get(tag().get()).map(holders -> holders.stream()
                    .map(Holder::unwrapKey)
                    .filter(Optional::isPresent)
                    .map(Optional::get).toList())
                    .orElse(List.of()));
        return list;
    }
}
