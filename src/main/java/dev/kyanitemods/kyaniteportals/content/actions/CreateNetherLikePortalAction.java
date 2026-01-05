package dev.kyanitemods.kyaniteportals.content.actions;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.actions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.util.BlockEntityPair;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
//? if <1.21.11 {
/*import net.minecraft.BlockUtil;
*///? } else
import net.minecraft.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

public class CreateNetherLikePortalAction extends PortalAction<CreateNetherLikePortalAction> {
    //$ map_codec_swap CreateNetherLikePortalAction
    public static final MapCodec<CreateNetherLikePortalAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation(),
            BlockEntityPair.CODEC.fieldOf("frame_block").forGetter(CreateNetherLikePortalAction::getFrameBlock),
            BlockEntityPair.CODEC.fieldOf("portal_block").forGetter(CreateNetherLikePortalAction::getPortalBlock),
            Size.CODEC.optionalFieldOf("size", new Size(4, 5)).forGetter(CreateNetherLikePortalAction::getSize),
            Codec.BOOL.optionalFieldOf("teleport_player_to_portal", true).forGetter(CreateNetherLikePortalAction::shouldTeleportPlayerToPortal)
    ).apply(instance, CreateNetherLikePortalAction::new));

    private final BlockEntityPair frame;
    private final BlockEntityPair portal;
    private final Size size;
    private final boolean teleportPlayer;

    public CreateNetherLikePortalAction(Settings settings, BlockEntityPair frame, BlockEntityPair portal, Size size, boolean teleportPlayer) {
        super(settings);
        this.frame = frame;
        this.portal = portal;
        this.size = size;
        this.teleportPlayer = teleportPlayer;
    }

    @Override
    public PortalActionType<CreateNetherLikePortalAction> getType() {
        return PortalActions.CREATE_NETHER_LIKE_PORTAL;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, Entity entity, ActionExecutionData data) {
        if (level.isClientSide()) return PortalActionResult.FAILURE;

        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);

        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty()) return PortalActionResult.FAILURE;
        final ServerLevel serverLevel = optional.get();

        EnumProperty<Direction.Axis> axisProperty = level.getBlockState(pos).hasProperty(BlockStateProperties.AXIS) ? BlockStateProperties.AXIS : BlockStateProperties.HORIZONTAL_AXIS;
        Direction.Axis axis = level.getBlockState(pos).getOptionalValue(axisProperty).orElse(Direction.Axis.X);

        BlockPos searchPos = BlockPos.containing(location.position().x(), location.position().y(), location.position().z());
        Optional<BlockUtil.FoundRectangle> optionalPortal = createPortal(serverLevel, searchPos, axis);

        if (shouldTeleportPlayerToPortal() && entity != null) {
            optionalPortal.map(foundRectangle ->
                    KyanitePortalsUtil.getDimensionTransitionFromExit(level, pos, serverLevel, foundRectangle, axis, entity)
            ).ifPresent(info -> KyanitePortalsUtil.teleport(entity, serverLevel, info));
        }

        return PortalActionResult.SUCCESS;
    }

    private Optional<BlockUtil.FoundRectangle> createPortal(ServerLevel serverLevel, BlockPos startPos, Direction.Axis axis) {
        Direction direction = axis == Direction.Axis.Y ? Direction.EAST : Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction directionCW = axis == Direction.Axis.Y ? Direction.UP : direction.getClockWise();
        double placementDistance = -1.0;
        BlockPos placementPos = null;
        double fallbackPlacementDistance = -1.0;
        BlockPos fallbackPlacementPos = null;
        WorldBorder worldBorder = serverLevel.getWorldBorder();
        int maxPlaceableY = Math.min(serverLevel.getMaxY(), serverLevel.getMinY() + serverLevel.getLogicalHeight()) - 1;
        BlockPos.MutableBlockPos mutablePos = startPos.mutable();

        for (BlockPos.MutableBlockPos columnPos : BlockPos.spiralAround(startPos, 16, Direction.EAST, Direction.SOUTH)) {
            int height = Math.min(maxPlaceableY, serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, columnPos.getX(), columnPos.getZ()));
            if (worldBorder.isWithinBounds(columnPos) && worldBorder.isWithinBounds(columnPos.move(direction, 1))) {
                columnPos.move(direction.getOpposite(), 1);

                for (int y = height; y >= serverLevel.getMinY(); y--) {
                    columnPos.setY(y);
                    if (!canPortalReplaceBlock(serverLevel, columnPos)) continue;
                    int firstEmptyY = y;

                    while (y > serverLevel.getMinY() && canPortalReplaceBlock(serverLevel, columnPos.move(Direction.DOWN))) {
                        y--;
                    }

                    if ((axis != Direction.Axis.Y && y + getSize().height() - 1 > maxPlaceableY) || y > maxPlaceableY) continue;

                    int deltaY = firstEmptyY - y;
                    if (deltaY > 0 && axis != Direction.Axis.Y && deltaY < getSize().heightWithoutFrame()) continue;

                    columnPos.setY(y);
                    if (this.canHostFrame(serverLevel, columnPos, mutablePos, direction, directionCW, 0)) {
                        double distance = startPos.distSqr(columnPos);
                        if (this.canHostFrame(serverLevel, columnPos, mutablePos, direction, directionCW, -1)
                                && this.canHostFrame(serverLevel, columnPos, mutablePos, direction, directionCW, 1)
                                && (placementDistance == -1.0 || placementDistance > distance)) {
                            placementDistance = distance;
                            placementPos = columnPos.immutable();
                        }

                        if (placementDistance == -1.0 && (fallbackPlacementDistance == -1.0 || fallbackPlacementDistance > distance)) {
                            fallbackPlacementDistance = distance;
                            fallbackPlacementPos = columnPos.immutable();
                        }
                    }
                }
            }
        }

        if (placementPos == null && fallbackPlacementPos != null) {
            placementPos = fallbackPlacementPos;
        }

        if (placementPos == null) {
            int minStartY = Math.max(serverLevel.getMinY() + 1, Math.min(serverLevel.getMaxY() - getSize().height(), 70));
            int maxStartY = maxPlaceableY - (getSize().height() + 4);
            if (maxStartY < minStartY) {
                return Optional.empty();
            }

            placementPos = new BlockPos(startPos.getX(), Mth.clamp(startPos.getY(), minStartY, maxStartY), startPos.getZ()).immutable();

            if (!worldBorder.isWithinBounds(placementPos)) {
                return Optional.empty();
            }

            createBasePlate(serverLevel, placementPos, direction, directionCW);
        }

        for (int offsetX = -1; offsetX < getSize().width() - 1; offsetX++) {
            for (int offsetY = -1; offsetY < getSize().height() - 1; offsetY++) {
                if (offsetX == -1 || offsetX == getSize().widthWithoutFrame() || offsetY == -1 || offsetY == getSize().heightWithoutFrame()) {
                    if (directionCW == Direction.UP) {
                        mutablePos.setWithOffset(placementPos, offsetX, 0, offsetY);
                    } else {
                        mutablePos.setWithOffset(placementPos, offsetX * direction.getStepX(), offsetY, offsetX * direction.getStepZ());
                    }
                    getFrameBlock().set(serverLevel, mutablePos, Block.UPDATE_ALL);
                }
            }
        }

        BlockEntityPair portalBlock = getPortalBlock();
        if (portalBlock.state().hasProperty(BlockStateProperties.AXIS)) {
            portalBlock = portalBlock.with(portalBlock.state().setValue(BlockStateProperties.AXIS, axis));
        } else if (portalBlock.state().hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            portalBlock = portalBlock.with(portalBlock.state().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis));
        }

        for (int offsetX = 0; offsetX < getSize().widthWithoutFrame(); offsetX++) {
            for (int offsetY = 0; offsetY < getSize().heightWithoutFrame(); offsetY++) {
                if (directionCW == Direction.UP) {
                    mutablePos.setWithOffset(placementPos, offsetX, 0, offsetY);
                } else {
                    mutablePos.setWithOffset(placementPos, offsetX * direction.getStepX(), offsetY, offsetX * direction.getStepZ());
                }
                portalBlock.set(serverLevel, mutablePos, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
            }
        }

        return Optional.of(new BlockUtil.FoundRectangle(placementPos.immutable(), 2, 3));
    }

    private static boolean canPortalReplaceBlock(ServerLevel serverLevel, BlockPos.MutableBlockPos mutablePos) {
        BlockState blockState = serverLevel.getBlockState(mutablePos);
        return blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
    }

    private void createBasePlate(ServerLevel level, BlockPos pos, Direction direction, Direction directionCW) {
        BlockPos.MutableBlockPos mutablePos = BlockPos.ZERO.mutable();

        if (directionCW == Direction.UP) {
            for (int offsetZ = -2; offsetZ < getSize().height(); offsetZ++) {
                for (int offsetX = -2; offsetX < getSize().width(); offsetX++) {
                    for (int y = -1; y < 3; y++) {
                        mutablePos.setWithOffset(pos, offsetX, y, offsetZ);
                        if (y < 0) {
                            getFrameBlock().set(level, mutablePos, Block.UPDATE_ALL);
                        } else {
                            level.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        } else {
            for (int offsetZ = -1; offsetZ < 2; offsetZ++) {
                for (int offsetX = 0; offsetX < getSize().widthWithoutFrame(); offsetX++) {
                    for (int y = -1; y < getSize().heightWithoutFrame(); y++) {
                        mutablePos.setWithOffset(pos, offsetX * direction.getStepX() + offsetZ * directionCW.getStepX(), y, offsetX * direction.getStepZ() + offsetZ * directionCW.getStepZ());
                        if (y < 0) {
                            getFrameBlock().set(level, mutablePos, Block.UPDATE_ALL);
                        } else {
                            level.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    private boolean canHostFrame(ServerLevel serverLevel, BlockPos currentPos, BlockPos.MutableBlockPos mutablePos, Direction direction, Direction directionCW, int offset) {
        for (int offsetX = -1; offsetX < getSize().width() - 1; offsetX++) {
            for (int offsetY = -1; offsetY < getSize().height() - 1; offsetY++) {
                if (direction == Direction.UP) {
                    mutablePos.setWithOffset(currentPos, direction.getStepX() * offsetX + directionCW.getStepX() * offset, offsetY, direction.getStepZ() * offsetX + directionCW.getStepZ() * offset);
                } else {
                    mutablePos.setWithOffset(currentPos, direction.getStepX() * offsetX + directionCW.getStepX() * offset, offsetY, direction.getStepZ() * offsetX + directionCW.getStepZ() * offset);
                }
                if (offsetY < 0 && !serverLevel.getBlockState(mutablePos).isSolid()) {
                    return false;
                }

                if (offsetY >= 0 && !canPortalReplaceBlock(serverLevel, mutablePos)) {
                    return false;
                }
            }
        }

        return true;
    }

    public BlockEntityPair getFrameBlock() {
        return frame;
    }

    public BlockEntityPair getPortalBlock() {
        return portal;
    }

    public Size getSize() {
        return size;
    }

    public boolean shouldTeleportPlayerToPortal() {
        return teleportPlayer;
    }

    public record Size(int width, int height) {
        public static final Codec<Size> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Size::width),
                ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Size::height)
        ).apply(instance, Size::new));

        public int widthWithoutFrame() {
            return width() - 2;
        }

        public int heightWithoutFrame() {
            return height() - 2;
        }
    }
}
