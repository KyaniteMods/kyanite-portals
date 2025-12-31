package dev.kyanitemods.kyaniteportals.content.portalactions.location;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.ActionExecutionData;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import dev.kyanitemods.kyaniteportals.util.DimensionList;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
//? if <1.21.11 {
import net.minecraft.Util;
//? } else
//import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record FullActionLocationOptions(DimensionContext dimension, PositionContext position) implements ActionLocationOptions {
    private static final Codec<FullActionLocationOptions> OPTIONAL_POSITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DimensionContext.CODEC.fieldOf("dimension").forGetter(FullActionLocationOptions::dimension),
            PositionContext.CODEC.optionalFieldOf("position", PositionContext.DEFAULT).forGetter(FullActionLocationOptions::position)
    ).apply(instance, FullActionLocationOptions::new));

    private static final Codec<FullActionLocationOptions> OPTIONAL_DIMENSION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DimensionContext.CODEC.optionalFieldOf("dimension", DimensionContext.DEFAULT).forGetter(FullActionLocationOptions::dimension),
            PositionContext.CODEC.fieldOf("position").forGetter(FullActionLocationOptions::position)
    ).apply(instance, FullActionLocationOptions::new));

    public static final Codec<FullActionLocationOptions> CODEC = CodecHelper.simpleEither(OPTIONAL_POSITION_CODEC, OPTIONAL_DIMENSION_CODEC);

    public static final FullActionLocationOptions DEFAULT = new FullActionLocationOptions(InEntryPoint.INSTANCE, PositionContext.DEFAULT);

    @Override
    public ActionLocation get(Level level, BlockPos pos, @Nullable Entity entity, RandomSource random, ActionExecutionData data) throws IllegalArgumentException {
        try {
            ResourceKey<LevelStem> dimension = dimension().get(level, entity, random);
            Vec3 position = position().get(level, pos, entity, dimension);
            return new ActionLocation(dimension, position);
        } catch (Throwable var9) {
            CrashReport crashReport = CrashReport.forThrowable(var9, "Calculating portal action location");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Location options details");
            crashReportCategory.setDetail("Entry dimension", KyanitePortalsUtil.getIdentifier(level.dimension()));
            if (entity != null) {
                crashReportCategory.setDetail("Entity dimension", KyanitePortalsUtil.getIdentifier(entity.level().dimension()));
            }
            crashReportCategory.setDetail("Dimension", dimension());
            crashReportCategory.setDetail("Position", position());
            throw new ReportedException(crashReport);
        }
    }

    public enum DimensionContextType implements StringRepresentable {
        ENTRY_POINT("entry_point", InEntryPoint.MAP_CODEC, InEntryPoint.CODEC),
        OPPOSITE_POINT("opposite_point", InOppositePoint.MAP_CODEC, InOppositePoint.CODEC),
        ENTITY_DIMENSION("entity_dimension", InEntityDimension.MAP_CODEC, InEntityDimension.CODEC),
        TAG("tag", InTag.MAP_CODEC, InTag.CODEC); //TODO: change to DimensionList

        public static final Codec<DimensionContextType> CODEC = StringRepresentable.fromEnum(DimensionContextType::values);

        private final String key;
        private final MapCodec<? extends DimensionContext> mapCodec;
        private final Codec<? extends DimensionContext> codec;

        DimensionContextType(String key, MapCodec<? extends DimensionContext> mapCodec, Codec<? extends DimensionContext> codec) {
            this.key = key;
            this.mapCodec = mapCodec;
            this.codec = codec;
        }

        public Codec<? extends DimensionContext> codec() {
            return codec;
        }

        public MapCodec<? extends DimensionContext> mapCodec() {
            return mapCodec;
        }

        @Override
        public String getSerializedName() {
            return key;
        }
    }

    public interface DimensionContext {
        Codec<DimensionContext> CODEC = DimensionContextType.CODEC.dispatch("in", DimensionContext::getType, /*? if <1.20.6 {*/DimensionContextType::codec/*? } else {*//*DimensionContextType::mapCodec*//*? }*/);
        DimensionContext DEFAULT = InEntryPoint.INSTANCE;

        ResourceKey<LevelStem> get(Level level, @Nullable Entity entity, RandomSource random);

        DimensionContextType getType();
    }

    public static class InEntryPoint implements DimensionContext {
        public static final InEntryPoint INSTANCE = new InEntryPoint();
        public static final MapCodec<InEntryPoint> MAP_CODEC = CodecHelper.unitMapCodec(INSTANCE);
        public static final Codec<InEntryPoint> CODEC = CodecHelper.unitCodec(INSTANCE);

        @Override
        public ResourceKey<LevelStem> get(Level level, Entity entity, RandomSource random) {
            return Registries.levelToLevelStem(level.dimension());
        }

        @Override
        public DimensionContextType getType() {
            return DimensionContextType.ENTRY_POINT;
        }
    }

    public record InOppositePoint(Optional<DimensionList> first, DimensionList second) implements DimensionContext {
        public static final MapCodec<InOppositePoint> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                DimensionList.CODEC.optionalFieldOf("first").forGetter(InOppositePoint::first),
                DimensionList.CODEC.fieldOf("second").forGetter(InOppositePoint::second)
        ).apply(instance, InOppositePoint::new));
        public static final Codec<InOppositePoint> CODEC = MAP_CODEC.codec();

        @Override
        public ResourceKey<LevelStem> get(Level level, Entity entity, RandomSource random) {
            if (level.isClientSide()) throw new UnsupportedOperationException("Cannot use opposite_point dimension option on a client");
            HolderLookup.RegistryLookup<LevelStem> registry = level.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);

            List<ResourceKey<LevelStem>> list;
            if (second().matches(level)) {
                list = first()
                        .map(dimensionList -> dimensionList.get(registry))
                        .orElse(registry.listElements()
                                .map(Holder.Reference::unwrapKey)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList());
            } else {
                list = second().get(registry);
            }

            Optional<ResourceKey<LevelStem>> optional = Util.getRandomSafe(list, random);
            if (optional.isEmpty()) throw new IllegalStateException("Dimension list (dimensions + tag) is empty");
            return optional.get();
        }

        @Override
        public DimensionContextType getType() {
            return DimensionContextType.OPPOSITE_POINT;
        }

        @Override
        public String toString() {
            return "InOppositePoint{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

    public static class InEntityDimension implements DimensionContext {
        public static final InEntityDimension INSTANCE = new InEntityDimension();
        public static final MapCodec<InEntityDimension> MAP_CODEC = CodecHelper.unitMapCodec(INSTANCE);
        public static final Codec<InEntityDimension> CODEC = CodecHelper.unitCodec(INSTANCE);

        @Override
        public ResourceKey<LevelStem> get(Level level, @Nullable Entity entity, RandomSource random) {
            if (entity == null) throw new UnsupportedOperationException("Cannot use dimension location type entity_dimension without entity context (tick actions)");
            return Registries.levelToLevelStem(entity.level().dimension());
        }

        @Override
        public DimensionContextType getType() {
            return DimensionContextType.ENTITY_DIMENSION;
        }
    }

    public record InTag(TagKey<LevelStem> tag) implements DimensionContext {
        public static final MapCodec<InTag> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.hashedCodec(Registries.LEVEL_STEM).fieldOf("tag").forGetter(InTag::tag)
        ).apply(instance, InTag::new));
        public static final Codec<InTag> CODEC = MAP_CODEC.codec();

        @Override
        public ResourceKey<LevelStem> get(Level level, Entity entity, RandomSource random) {
            if (level.isClientSide()) throw new UnsupportedOperationException("Cannot use tag dimension option on a client");
            HolderLookup.RegistryLookup<LevelStem> registry = level.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);

            TagKey<LevelStem> opposite = tag();
            Optional<ResourceKey<LevelStem>> optional = registry.get(opposite)
                    .flatMap(holders -> Util.getRandomSafe(holders.stream().toList(), random))
                    .flatMap(Holder::unwrapKey);
            if (optional.isEmpty()) throw new IllegalStateException("Dimension tag " + opposite.location() + " does not exist");
            return optional.get();
        }

        @Override
        public DimensionContextType getType() {
            return DimensionContextType.TAG;
        }

        @Override
        public String toString() {
            return "InTag{" +
                    "tag=" + tag +
                    '}';
        }
    }

    public record PositionContext(From from, RoundingMode roundingMode, boolean inBounds, boolean adaptCoordinateSpace, Vec3 offset) {
        public static final PositionContext DEFAULT = new PositionContext(From.PORTAL, RoundingMode.NONE, false, false, Vec3.ZERO);

        public static final Codec<PositionContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                From.CODEC.fieldOf("from").forGetter(PositionContext::from),
                RoundingMode.CODEC.optionalFieldOf("rounding_mode", RoundingMode.NONE).forGetter(PositionContext::roundingMode),
                Codec.BOOL.optionalFieldOf("in_bounds", false).forGetter(PositionContext::inBounds),
                Codec.BOOL.optionalFieldOf("adapt_coordinate_space", false).forGetter(PositionContext::adaptCoordinateSpace),
                Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(PositionContext::offset)
        ).apply(instance, PositionContext::new));

        public Vec3 get(Level level, BlockPos pos, @Nullable Entity entity, ResourceKey<LevelStem> toKey) {
            Vec3 position = from().get(level, pos, entity);
            Level to;
            if ((adaptCoordinateSpace() || inBounds()) && level.isClientSide()) {
                throw new UnsupportedOperationException("Cannot use adapt_coordinate_space or in_bounds on a client");
            } else {
                if (KyanitePortalsUtil.getIdentifier(toKey).equals(KyanitePortalsUtil.getIdentifier(Registries.levelToLevelStem(level.dimension())))) { // probably unnecessary to call Registries.levelToLevelStem()?
                    to = level;
                } else if (level.isClientSide()) {
                    throw new IllegalArgumentException("Cannot execute action in different dimension on a client (position options)");
                } else {
                    to = Objects.requireNonNull(level.getServer().getLevel(Registries.levelStemToLevel(toKey)), "Dimension " + KyanitePortalsUtil.getIdentifier(toKey) + " does not exist");
                }
            }

            if (adaptCoordinateSpace()) {
                Optional<ResourceKey<LevelStem>> referenceKey = from().getDimension(level, pos, entity);
                DimensionType reference = referenceKey.map(levelStemResourceKey -> Objects.requireNonNull(level.getServer().getLevel(Registries.levelStemToLevel(levelStemResourceKey))).dimensionType()).orElseGet(level::dimensionType);
                DimensionType toType = Objects.requireNonNull(level.getServer().getLevel(Registries.levelStemToLevel(toKey))).dimensionType();
                double scale = DimensionType.getTeleportationScale(reference, toType);
                position = position.multiply(scale, 1.0, scale);
            }
            if (inBounds()) {
                WorldBorder border = to.getWorldBorder();
                position = new Vec3(Mth.clamp(position.x(), border.getMinX(), border.getMaxX()), position.y(), Mth.clamp(position.z(), border.getMinZ(), border.getMaxZ()));
            }
            position = roundingMode().apply(position);
            return position.add(offset());
        }

        public enum RoundingMode implements StringRepresentable {
            NONE("none", vec3 -> vec3),
            UP("up", vec3 -> {
                double x = vec3.x() > 0 ? Math.ceil(vec3.x()) : Math.floor(vec3.x());
                double y = vec3.y() > 0 ? Math.ceil(vec3.y()) : Math.floor(vec3.y());
                double z = vec3.z() > 0 ? Math.ceil(vec3.z()) : Math.floor(vec3.z());
                return new Vec3(x, y, z);
            }),
            DOWN("down", vec3 -> {
                double x = vec3.x() > 0 ? Math.floor(vec3.x()) : Math.ceil(vec3.x());
                double y = vec3.y() > 0 ? Math.floor(vec3.y()) : Math.ceil(vec3.y());
                double z = vec3.z() > 0 ? Math.floor(vec3.z()) : Math.ceil(vec3.z());
                return new Vec3(x, y, z);
            }),
            ROUND("round", vec3 -> {
                double x = Math.round(vec3.x());
                double y = Math.round(vec3.y());
                double z = Math.round(vec3.z());
                return new Vec3(x, y, z);
            }),
            FLOOR("floor", vec3 -> {
                double x = Math.floor(vec3.x());
                double y = Math.floor(vec3.y());
                double z = Math.floor(vec3.z());
                return new Vec3(x, y, z);
            }),
            CEILING("ceiling", vec3 -> {
                double x = Math.ceil(vec3.x());
                double y = Math.ceil(vec3.y());
                double z = Math.ceil(vec3.z());
                return new Vec3(x, y, z);
            });

            public static final Codec<RoundingMode> CODEC = StringRepresentable.fromEnum(RoundingMode::values);

            private final String key;
            private final Function<Vec3, Vec3> operator;

            RoundingMode(String key, Function<Vec3, Vec3> operator) {
                this.key = key;
                this.operator = operator;
            }

            public Vec3 apply(Vec3 value) {
                return operator.apply(value);
            }

            @Override
            public String toString() {
                return getSerializedName();
            }

            @Override
            public String getSerializedName() {
                return key;
            }
        }

        public enum From implements StringRepresentable {
            PORTAL("portal"),
            ENTITY("entity"),
            WORLD_ORIGIN("world_origin");

            public static final Codec<From> CODEC = StringRepresentable.fromEnum(From::values);

            private final String key;

            From(String key) {
                this.key = key;
            }

            public Optional<ResourceKey<LevelStem>> getDimension(Level level, BlockPos pos, @Nullable Entity entity) {
                if (this == PORTAL) return Optional.of(Registries.levelToLevelStem(level.dimension()));
                else if (this == ENTITY) {
                    if (entity == null) throw new UnsupportedOperationException("Cannot use position context from entity without entity context (tick actions)");
                    return Optional.of(Registries.levelToLevelStem(entity.level().dimension()));
                }
                return Optional.empty();
            }

            public Vec3 get(Level level, BlockPos pos, @Nullable Entity entity) {
                if (this == PORTAL) return pos.getCenter();
                else if (this == ENTITY) {
                    if (entity == null) throw new UnsupportedOperationException("Cannot use position context from entity without entity context (tick actions)");
                    return entity.position();
                }
                return Vec3.ZERO;
            }

            @Override
            public String toString() {
                return getSerializedName();
            }

            @Override
            public String getSerializedName() {
                return key;
            }
        }

        @Override
        public String toString() {
            return "PositionContext{" +
                    "from=" + from +
                    ", roundingMode=" + roundingMode +
                    ", inBounds=" + inBounds +
                    ", adaptCoordinateSpace=" + adaptCoordinateSpace +
                    ", offset=" + offset +
                    '}';
        }
    }
}
