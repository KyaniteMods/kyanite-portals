package dev.kyanitemods.kyaniteportals.api;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.actions.*;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.content.generators.NetherLikePortalGenerator;
import dev.kyanitemods.kyaniteportals.content.particles.CustomPortalParticleOptions;
import dev.kyanitemods.kyaniteportals.content.actions.*;
import dev.kyanitemods.kyaniteportals.content.actions.location.FullActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.actions.location.LoadActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlocks;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.content.testers.RectanglePortalTester;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.util.BlockEntityPair;
import dev.kyanitemods.kyaniteportals.util.DimensionList;
import dev.kyanitemods.kyaniteportals.util.Range;
import dev.kyanitemods.kyaniteportals.util.BlockPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
//? if >=1.21.3 {
import net.minecraft.core.HolderGetter;
//? }
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class SimplePortalBuilder {
    private SimplePortalBuilder() {}

    @ApiStatus.Internal
    public static final TagKey<PoiType> POI_TAG = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, KyanitePortals.id("custom_portals"));

    private int color = 0xFFFFFF;
    private Supplier<Optional<ParticleOptions>> particleOptions = () -> Optional.of(new CustomPortalParticleOptions(new Vector3f(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f)));
    private ResourceKey<LevelStem> toDimension = LevelStem.NETHER;
    private ResourceKey<LevelStem> fromDimension;
    private List<Function<RegistryOps.RegistryInfoLookup, PortalTriggerInstance<?>>> ignition = new ArrayList<>();
    private List<Block> ignitionBlocks = new ArrayList<>();
    private BlockPredicate frame = BlockPredicate.Builder.block().build();
    private Supplier<BlockPredicate> replaceable = () -> {
        List<Block> blocks = new ArrayList<>();
        blocks.add(Blocks.AIR);
        blocks.add(Blocks.CAVE_AIR);
        blocks.add(Blocks.VOID_AIR);
        blocks.addAll(ignitionBlocks);
        Block[] arr = blocks.toArray(new Block[0]);
        return BlockPredicate.Builder.block().of(arr).build();
    };
    private Range.Int width = Range.Int.create(4, 23);
    private Range.Int height = Range.Int.create(5, 23);

    public static SimplePortalBuilder create() {
        return new SimplePortalBuilder();
    }

    public SimplePortalBuilder noParticles() {
        particleOptions = Optional::empty;
        return this;
    }

    public SimplePortalBuilder particle(ParticleOptions options) {
        particleOptions = () -> Optional.ofNullable(options);
        return this;
    }

    public SimplePortalBuilder color(int color) {
        this.color = color;
        return this;
    }

    public SimplePortalBuilder fromDimension(ResourceKey<LevelStem> key) {
        this.fromDimension = key;
        return this;
    }

    public SimplePortalBuilder toDimension(ResourceKey<LevelStem> key) {
        this.toDimension = key;
        return this;
    }

    public SimplePortalBuilder ignition(Item item, int damage) {
        //? if <1.21.3 {
        /*ignition.add(provider -> PortalTriggers.USE_ITEM.create(ItemPredicate.Builder.item().of(item).build(), damage));
        *///? } else {
        ignition.add(provider -> {
            HolderGetter<Item> itemLookup = provider.lookup(Registries.ITEM).orElseThrow().getter();
            return PortalTriggers.USE_ITEM.create(ItemPredicate.Builder.item().of(itemLookup, item).build(), damage);
        });
        //? }
        return this;
    }

    public SimplePortalBuilder ignition(Item... items) {
        //? if <1.21.3 {
        /*ignition.add(provider -> PortalTriggers.USE_ITEM.create(ItemPredicate.Builder.item().of(items).build()));
        *///? } else {
        ignition.add(provider -> {
            HolderGetter<Item> itemLookup = provider.lookup(Registries.ITEM).orElseThrow().getter();
            return PortalTriggers.USE_ITEM.create(ItemPredicate.Builder.item().of(itemLookup, items).build());
        });
        //? }
        return this;
    }

    public SimplePortalBuilder ignition(Block block) {
        ignition.add(provider -> PortalTriggers.BLOCK_CHANGE.create(block.defaultBlockState()));
        ignitionBlocks.add(block);
        return this;
    }

    public SimplePortalBuilder frame(BlockPredicate predicate) {
        frame = predicate;
        return this;
    }

    public SimplePortalBuilder frame(Block... blocks) {
        return frame(BlockPredicate.Builder.block().of(blocks).build());
    }

    public SimplePortalBuilder replaceable(BlockPredicate predicate) {
        replaceable = () -> predicate;
        return this;
    }

    public SimplePortalBuilder replaceable(Block... blocks) {
        return replaceable(BlockPredicate.Builder.block().of(blocks).build());
    }

    public SimplePortalBuilder width(int min, int max) {
        width = Range.Int.create(min, max);
        return this;
    }

    public SimplePortalBuilder height(int min, int max) {
        height = Range.Int.create(min, max);
        return this;
    }

    public ResourceKey<Portal> register(Identifier id) {
        ResourceKey<Portal> key = ResourceKey.create(KyanitePortals.RESOURCE_KEY, id);
        CustomPortalBlockEntity.COLORS.put(key, color);
        KyanitePortals.PORTAL_REGISTRY_OVERRIDES.put(key, provider -> {
            //? if >=1.21.3
            HolderGetter<EntityType<?>> entityLookup = provider.lookup(Registries.ENTITY_TYPE).orElseThrow().getter();
            CompoundTag tag = new CompoundTag();
            tag.putString("portal", id.toString());
            BlockPredicate portalPredicate = BlockPredicate.Builder.block().of(KyanitePortalsBlocks.CUSTOM_PORTAL).hasNbt(tag).build();
            Portal.Builder builder = Portal.Builder.create()
                    .withGenerator(new NetherLikePortalGenerator(
                            ignition.stream().map(f -> f.apply(provider)).collect(Collectors.toUnmodifiableList()),
                            new DimensionList(Set.of(fromDimension, toDimension), Optional.empty()),
                            new BlockEntityPair(KyanitePortalsBlocks.CUSTOM_PORTAL.defaultBlockState(), tag)))
                    .withTester(new RectanglePortalTester(
                            width,
                            height,
                            frame,
                            replaceable.get(),
                            portalPredicate
                    ))
                    .withEnterActions(new PlayLocalSoundAction(PortalAction.Settings.DEFAULT, Holder.direct(SoundEvents.PORTAL_TRIGGER), ConstantFloat.of(0.25f), UniformFloat.of(0.8f, 1.2f)))
                    .withTravelActions(
                            new StoreActionLocationAction(
                                    PortalAction.Settings.Builder.create().locationOptions(
                                            new FullActionLocationOptions(
                                                    new FullActionLocationOptions.InOppositePoint(
                                                            fromDimension == null ? Optional.empty() : Optional.of(new DimensionList(Set.of(fromDimension), Optional.empty())),
                                                            new DimensionList(Set.of(toDimension), Optional.empty())
                                                    ),
                                                    FullActionLocationOptions.PositionContext.DEFAULT
                                            )).build(), "location"),
                            new TeleportToNetherLikePortalPoiAction(
                                    PortalAction.Settings.Builder.create()
                                            .locationOptions(new LoadActionLocationOptions("location"))
                                            .onFailure(
                                                    new CreateNetherLikePortalAction(
                                                            PortalAction.Settings.Builder.create()
                                                                    .predicate(EntityPredicate.Builder.entity().of(/*? if >=1.21.3 {*/entityLookup, /*? }*/EntityType.PLAYER).build())
                                                                    .locationOptions(new LoadActionLocationOptions("location"))
                                                                    .build(),
                                                            new BlockEntityPair(Blocks.OBSIDIAN.defaultBlockState(), new CompoundTag()),
                                                            new BlockEntityPair(KyanitePortalsBlocks.CUSTOM_PORTAL.defaultBlockState(), tag),
                                                            new CreateNetherLikePortalAction.Size(4, 5),
                                                            true
                                                    )
                                            )
                                            .build(),
                                    POI_TAG,
                                    portalPredicate,
                                    128,
                                    true
                            ),
                            new PlayLocalSoundAction(
                                    PortalAction.Settings.Builder.create()
                                            .locationOptions(
                                                    new FullActionLocationOptions(
                                                            FullActionLocationOptions.InEntityDimension.INSTANCE,
                                                            new FullActionLocationOptions.PositionContext(
                                                                    FullActionLocationOptions.PositionContext.From.ENTITY,
                                                                    FullActionLocationOptions.PositionContext.RoundingMode.NONE,
                                                                    false,
                                                                    false,
                                                                    Vec3.ZERO)))
                                            .build(),
                                    Holder.direct(SoundEvents.PORTAL_TRAVEL),
                                    ConstantFloat.of(0.25f),
                                    UniformFloat.of(0.8f, 1.2f)
                            )
                    )
                    .withAnimationTickActions(
                            new PlayLocalSoundAction(
                                    PortalAction.Settings.Builder.create()
                                            .probability(0.01f)
                                            .environment(PortalActionEnvironment.CLIENT)
                                            .build(),
                                    Holder.direct(SoundEvents.PORTAL_AMBIENT),
                                    ConstantFloat.of(0.25f),
                                    UniformFloat.of(0.8f, 1.2f)
                            )
                    );
            if (particleOptions.get().isPresent()) {
                builder.withAnimationTickActions(new SpawnNetherLikePortalParticlesAction(
                        PortalAction.Settings.Builder.create()
                                .locationOptions(new FullActionLocationOptions(
                                        FullActionLocationOptions.InEntryPoint.INSTANCE,
                                        new FullActionLocationOptions.PositionContext(
                                                FullActionLocationOptions.PositionContext.From.PORTAL,
                                                FullActionLocationOptions.PositionContext.RoundingMode.NONE,
                                                false,
                                                false,
                                                Vec3.ZERO)
                                ))
                                .environment(PortalActionEnvironment.CLIENT)
                                .build(),
                        ConstantInt.of(4),
                        particleOptions.get().get()
                ));
            }
            return builder.build();
        });

        return key;
    }
}
