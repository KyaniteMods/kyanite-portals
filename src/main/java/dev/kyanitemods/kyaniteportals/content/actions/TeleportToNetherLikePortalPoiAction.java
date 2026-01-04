package dev.kyanitemods.kyaniteportals.content.actions;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.actions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.util.BlockPredicate;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
//? if <1.21.11 {
/*import net.minecraft.BlockUtil;
*///? } else
import net.minecraft.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeleportToNetherLikePortalPoiAction extends PortalAction<TeleportToNetherLikePortalPoiAction> {
    //$ map_codec_swap TeleportToNetherLikePortalPoiAction
    public static final MapCodec<TeleportToNetherLikePortalPoiAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation(),
            TagKey.hashedCodec(Registries.POINT_OF_INTEREST_TYPE).fieldOf("point_of_interest_types").forGetter(TeleportToNetherLikePortalPoiAction::getPoiTypes),
            BlockPredicate.CODEC.optionalFieldOf("portal_predicate").xmap(optional -> optional.orElse(BlockPredicate.ANY), predicate -> predicate == BlockPredicate.ANY ? Optional.empty() : Optional.of(predicate)).forGetter(TeleportToNetherLikePortalPoiAction::getPortalPredicate),
            Codec.INT.fieldOf("search_range").forGetter(TeleportToNetherLikePortalPoiAction::getSearchRange),
            Codec.BOOL.optionalFieldOf("adapt_coordinate_space", true).forGetter(TeleportToNetherLikePortalPoiAction::shouldAdaptCoordinateSpace)
    ).apply(instance, TeleportToNetherLikePortalPoiAction::new));

    private final TagKey<PoiType> poiTypes;
    private final BlockPredicate portalPredicate;
    private final int searchRange;
    private final boolean adaptSearchToDimensionScale;

    public TeleportToNetherLikePortalPoiAction(Settings settings, TagKey<PoiType> poiTypes, BlockPredicate portalPredicate, int searchRange, boolean adaptSearchToDimensionScale) {
        super(settings);
        this.poiTypes = poiTypes;
        this.portalPredicate = portalPredicate;
        this.searchRange = searchRange;
        this.adaptSearchToDimensionScale = adaptSearchToDimensionScale;
    }

    @Override
    public PortalActionType<TeleportToNetherLikePortalPoiAction> getType() {
        return PortalActions.TELEPORT_TO_NETHER_LIKE_PORTAL_POI;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        if (entity == null || level.isClientSide()) return PortalActionResult.FAILURE;

        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);

        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty()) return PortalActionResult.FAILURE;
        final ServerLevel serverLevel = optional.get();

        BlockPos searchPos = BlockPos.containing(location.position().x(), location.position().y(), location.position().z());
        int searchRange;
        if (shouldAdaptCoordinateSpace()) {
            searchRange = Mth.ceil(getSearchRange() / serverLevel.dimensionType().coordinateScale());
        } else {
            searchRange = getSearchRange();
        }

        PoiManager poiManager = serverLevel.getPoiManager();
        poiManager.ensureLoadedAndValid(serverLevel, searchPos, searchRange);
        Optional<BlockUtil.FoundRectangle> optionalPortal = findPortalAround(serverLevel, getPoiTypes(), searchRange, searchPos, serverLevel.getWorldBorder(), getPortalPredicate());
        if (optionalPortal.isEmpty()) return PortalActionResult.FAILURE;

        EnumProperty<Direction.Axis> axisProperty = level.getBlockState(pos).hasProperty(BlockStateProperties.AXIS) ? BlockStateProperties.AXIS : BlockStateProperties.HORIZONTAL_AXIS;
        Direction.Axis axis = level.getBlockState(pos).getOptionalValue(axisProperty).orElse(Direction.Axis.X);

        optionalPortal.map(foundRectangle ->
                KyanitePortalsUtil.getDimensionTransitionFromExit(level, pos, serverLevel, foundRectangle, axis, entity)
        ).ifPresent(info -> KyanitePortalsUtil.teleport(entity, serverLevel, info));
        return PortalActionResult.SUCCESS;
    }

    //TODO: refactor
    private static Optional<BlockUtil.FoundRectangle> findPortalAround(final ServerLevel level, TagKey<PoiType> poiType, int i, BlockPos searchPos, WorldBorder worldBorder, BlockPredicate predicate) {
        PoiManager poiManager = level.getPoiManager();
        poiManager.ensureLoadedAndValid(level, searchPos, i);
        Optional<PoiRecord> optional = poiManager.getInSquare(holder -> holder.is(poiType), searchPos, i, PoiManager.Occupancy.ANY)
                .filter(poiRecord -> worldBorder.isWithinBounds(poiRecord.getPos()))
                .sorted(Comparator.comparingDouble((PoiRecord poiRecord) -> poiRecord.getPos().distSqr(searchPos)).thenComparingInt((PoiRecord poiRecord) -> poiRecord.getPos().getY()))
                .filter(poiRecord -> {
                    level.getBlockState(poiRecord.getPos());
                    return predicate.matches(level, poiRecord.getPos());
                })
                .findFirst();
        return optional.map(
                poiRecord -> {
                    BlockPos poiPos = poiRecord.getPos();
                    //? if <1.21.3 {
                    /*level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
                    *///? } else
                    level.getChunkSource().addTicketWithRadius(TicketType.PORTAL, new ChunkPos(poiPos), 3);
                    BlockState blockState = level.getBlockState(poiPos);
                    Direction.Axis axis = blockState.getOptionalValue(BlockStateProperties.AXIS).orElse(blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X));
                    return BlockUtil.getLargestRectangleAround(
                            poiPos,
                            axis,
                            21,
                            axis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y,
                            21,
                            pos -> level.getBlockState(pos) == blockState
                    );
                }
        );
    }

    public TagKey<PoiType> getPoiTypes() {
        return poiTypes;
    }

    public BlockPredicate getPortalPredicate() {
        return portalPredicate;
    }

    public int getSearchRange() {
        return searchRange;
    }

    public boolean shouldAdaptCoordinateSpace() {
        return adaptSearchToDimensionScale;
    }
}
