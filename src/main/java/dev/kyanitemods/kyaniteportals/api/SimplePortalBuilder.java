package dev.kyanitemods.kyaniteportals.api;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.actions.*;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.content.generators.NetherLikePortalGenerator;
import dev.kyanitemods.kyaniteportals.content.particles.CustomPortalParticleOptions;
import dev.kyanitemods.kyaniteportals.content.actions.location.FullActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.actions.location.LoadActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlocks;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.content.testers.RectanglePortalTester;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.util.BlockEntityPair;
import dev.kyanitemods.kyaniteportals.util.Range;
import dev.kyanitemods.kyaniteportals.util.blockpredicate.AnyOfBlockPredicate;
import dev.kyanitemods.kyaniteportals.util.blockpredicate.BlockPredicate;
import dev.kyanitemods.kyaniteportals.util.blockpredicate.SimpleBlockPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Direction;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.*;
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
    private BlockPredicate frame = SimpleBlockPredicate.Builder.block().build();
    private BlockEntityPair generatedFrame = null;
    private BlockPredicate replaceable = SimpleBlockPredicate.Builder.block().of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR).build();
    private Range.Int width = Range.Int.create(4, 23);
    private Range.Int height = Range.Int.create(5, 23);
    private int generatedWidth = 4;
    private int generatedHeight = 5;
    private Optional<Holder<SoundEvent>> ambientSound = Optional.of(Holder.direct(SoundEvents.PORTAL_AMBIENT));
    private Optional<Holder<SoundEvent>> travelSound = Optional.of(Holder.direct(SoundEvents.PORTAL_TRAVEL));
    private Optional<Holder<SoundEvent>> triggerSound = Optional.of(Holder.direct(SoundEvents.PORTAL_TRIGGER));
    private Set<Direction.Axis> axes = Set.of(Direction.Axis.X, Direction.Axis.Z);
    private boolean cornersRequired = false;

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

    public SimplePortalBuilder ignition(Function<RegistryOps.RegistryInfoLookup, PortalTriggerInstance<?>> triggerFunction) {
        ignition.add(triggerFunction);
        return this;
    }

    public SimplePortalBuilder ignition(PortalTriggerInstance<?> triggerInstance) {
        ignition.add(provider -> triggerInstance);
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

    public SimplePortalBuilder ignition(BlockPredicate predicate) {
        ignition.add(provider -> PortalTriggers.BLOCK_CHANGE.create(predicate));
        replaceable = new AnyOfBlockPredicate(List.of(replaceable, predicate));
        return this;
    }

    public SimplePortalBuilder ignition(Block... blocks) {
        SimpleBlockPredicate predicate = SimpleBlockPredicate.Builder.block().of(blocks).build();
        ignition.add(provider -> PortalTriggers.BLOCK_CHANGE.create(predicate));
        replaceable = new AnyOfBlockPredicate(List.of(replaceable, predicate));
        return this;
    }

    //? if <1.21 {
    /*public SimplePortalBuilder ignition(Potion... potions) {
        return ignition(Arrays.stream(potions).map(Holder::direct).toList());
    }
    *///? }

    public SimplePortalBuilder ignition(List<Holder<Potion>> potions) {
        ignition.add(provider -> PortalTriggers.THROWN_POTION.create(potions));
        return this;
    }

    @SafeVarargs
    public final SimplePortalBuilder ignition(Holder<Potion>... potions) {
        return ignition(Arrays.asList(potions));
    }

    public SimplePortalBuilder frame(BlockPredicate predicate) {
        frame = predicate;
        return this;
    }

    public SimplePortalBuilder frame(Block... blocks) {
        frame(SimpleBlockPredicate.Builder.block().of(blocks).build());
        if (generatedFrame == null) generatedFrame(blocks[0]);
        return this;
    }

    public SimplePortalBuilder generatedFrame(Block block) {
        return generatedFrame(block.defaultBlockState());
    }

    public SimplePortalBuilder generatedFrame(BlockState state) {
        return generatedFrame(new BlockEntityPair(state, new CompoundTag()));
    }

    public SimplePortalBuilder generatedFrame(BlockEntityPair pair) {
        generatedFrame = pair;
        return this;
    }

    public SimplePortalBuilder replaceable(BlockPredicate predicate) {
        replaceable = predicate;
        return this;
    }

    public SimplePortalBuilder replaceable(Function<BlockPredicate, BlockPredicate> function) {
        replaceable = function.apply(replaceable);
        return this;
    }

    public SimplePortalBuilder replaceable(Block... blocks) {
        return replaceable(SimpleBlockPredicate.Builder.block().of(blocks).build());
    }

    public SimplePortalBuilder addReplaceable(Block... blocks) {
        return replaceable(predicate -> new AnyOfBlockPredicate(List.of(predicate, SimpleBlockPredicate.Builder.block().of(blocks).build())));
    }

    public SimplePortalBuilder width(int min, int max) {
        width = Range.Int.create(min, max);
        return this;
    }

    public SimplePortalBuilder height(int min, int max) {
        height = Range.Int.create(min, max);
        return this;
    }

    public SimplePortalBuilder generatedSize(int width, int height) {
        generatedWidth = width;
        generatedHeight = height;
        return this;
    }

    public SimplePortalBuilder ambientSound(Holder<SoundEvent> sound) {
        ambientSound = Optional.ofNullable(sound);
        return this;
    }

    public SimplePortalBuilder travelSound(Holder<SoundEvent> sound) {
        travelSound = Optional.ofNullable(sound);
        return this;
    }

    public SimplePortalBuilder triggerSound(Holder<SoundEvent> sound) {
        triggerSound = Optional.ofNullable(sound);
        return this;
    }

    public SimplePortalBuilder vertical() {
        axes = Set.of(Direction.Axis.X, Direction.Axis.Z);
        return this;
    }

    public SimplePortalBuilder horizontal() {
        axes = Set.of(Direction.Axis.Y);
        return this;
    }

    public SimplePortalBuilder allAxes() {
        axes = Set.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z);
        return this;
    }

    public SimplePortalBuilder cornersRequired(boolean value) {
        cornersRequired = value;
        return this;
    }

    public SimplePortalBuilder cornersRequired() {
        return cornersRequired(true);
    }

    public ResourceKey<Portal> register(Identifier id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("portal", id.toString());
        BlockEntityPair pair = new BlockEntityPair(KyanitePortalsBlocks.CUSTOM_PORTAL.defaultBlockState(), tag);
        SimpleBlockPredicate portalPredicate = SimpleBlockPredicate.Builder.block().of(KyanitePortalsBlocks.CUSTOM_PORTAL).hasNbt(tag).build();
        ResourceKey<Portal> key = register(pair, portalPredicate, id);
        CustomPortalBlockEntity.COLORS.put(key, color);
        return key;
    }

    public ResourceKey<Portal> register(Block block, Identifier id) {
        return register(new BlockEntityPair(block.defaultBlockState(), new CompoundTag()), SimpleBlockPredicate.Builder.block().of(block).build(), id);
    }

    public ResourceKey<Portal> register(BlockEntityPair portal, SimpleBlockPredicate portalPredicate, Identifier id) {
        ResourceKey<Portal> key = ResourceKey.create(KyanitePortals.RESOURCE_KEY, id);
        KyanitePortals.PORTAL_REGISTRY_OVERRIDES.put(key, provider -> {
            //? if >=1.21.3
            HolderGetter<EntityType<?>> entityLookup = provider.lookup(Registries.ENTITY_TYPE).orElseThrow().getter();
            Portal.Builder builder = Portal.Builder.create()
                    .withGenerator(new NetherLikePortalGenerator(
                            ignition.stream().map(f -> f.apply(provider)).collect(Collectors.toUnmodifiableList()),
                            Set.of(fromDimension, toDimension),
                            portal))
                    .withTester(new RectanglePortalTester(
                            width,
                            height,
                            axes,
                            frame,
                            replaceable,
                            portalPredicate,
                            cornersRequired
                    ))
                    .withTravelActions(
                            new StoreActionLocationAction(
                                    PortalAction.Settings.Builder.create().locationOptions(
                                            new FullActionLocationOptions(
                                                    new FullActionLocationOptions.InOppositePoint(
                                                            fromDimension == null ? Optional.empty() : Optional.of(Set.of(fromDimension)),
                                                            Set.of(toDimension)
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
                                                            generatedFrame == null ? new BlockEntityPair(Blocks.OBSIDIAN.defaultBlockState(), new CompoundTag()) : generatedFrame,
                                                            portal,
                                                            new CreateNetherLikePortalAction.Size(generatedWidth, generatedHeight),
                                                            true
                                                    )
                                            )
                                            .build(),
                                    POI_TAG,
                                    portalPredicate,
                                    128,
                                    true
                            )
                    );
            triggerSound.ifPresent(soundEventHolder -> builder.withEnterActions(
                    new PlayLocalSoundAction(
                            PortalAction.Settings.DEFAULT,
                            soundEventHolder,
                            ConstantFloat.of(0.25f),
                            UniformFloat.of(0.8f, 1.2f)
                    )
            ));
            travelSound.ifPresent(soundEventHolder -> builder.withTravelActions(new PlayLocalSoundAction(
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
                    soundEventHolder,
                    ConstantFloat.of(0.25f),
                    UniformFloat.of(0.8f, 1.2f)
            )));
            ambientSound.ifPresent(soundEventHolder -> builder.withAnimationTickActions(
                    new PlayLocalSoundAction(
                            PortalAction.Settings.Builder.create()
                                    .probability(0.01f)
                                    .environment(PortalActionEnvironment.CLIENT)
                                    .build(),
                            soundEventHolder,
                            ConstantFloat.of(0.25f),
                            UniformFloat.of(0.8f, 1.2f)
                    )
            ));
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
