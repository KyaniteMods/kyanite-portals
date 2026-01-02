package dev.kyanitemods.kyaniteportals.content.portalactions;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;

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

        Direction.Axis axis = level.getBlockState(pos).getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);

        BlockPos searchPos = BlockPos.containing(location.position().x(), location.position().y(), location.position().z());
        Optional<BlockUtil.FoundRectangle> optionalPortal = createPortal(serverLevel, searchPos, axis);

        if (shouldTeleportPlayerToPortal() && entity != null) {
            optionalPortal.map(foundRectangle -> {
                BlockState blockState = level.getBlockState(pos);
                Vec3 vec3;
                if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                    BlockUtil.FoundRectangle foundRectangle2 = BlockUtil.getLargestRectangleAround(
                            pos, axis, 21, Direction.Axis.Y, 21, blockPos -> level.getBlockState(blockPos) == blockState
                    );
                    vec3 = PortalShape.getRelativePosition(foundRectangle2, axis, entity.position(), entity.getDimensions(entity.getPose()));
                } else {
                    vec3 = new Vec3(0.5, 0.0, 0.0);
                }

                return KyanitePortalsUtil.createTeleport(serverLevel, foundRectangle, axis, vec3, entity);
            }).ifPresent(info -> KyanitePortalsUtil.teleport(entity, serverLevel, info));
        }

        return PortalActionResult.SUCCESS;
    }

    private Optional<BlockUtil.FoundRectangle> createPortal(ServerLevel serverLevel, BlockPos startPos, Direction.Axis axis) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double distance1 = -1.0;
        BlockPos blockPos2 = null;
        double distance2 = -1.0;
        BlockPos blockPos3 = null;
        WorldBorder worldBorder = serverLevel.getWorldBorder();
        int maxHeight = Math.min(serverLevel.getMaxY(), serverLevel.getMinY() + serverLevel.getLogicalHeight()) - 1;
        BlockPos.MutableBlockPos mutablePos = startPos.mutable();

        for (BlockPos.MutableBlockPos currentPos : BlockPos.spiralAround(startPos, 16, Direction.EAST, Direction.SOUTH)) {
            int placementHeight = Math.min(maxHeight, serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, currentPos.getX(), currentPos.getZ()));
            if (worldBorder.isWithinBounds(currentPos) && worldBorder.isWithinBounds(currentPos.move(direction, 1))) {
                currentPos.move(direction.getOpposite(), 1);

                for (int currentY = placementHeight; currentY >= serverLevel.getMinY(); currentY--) {
                    currentPos.setY(currentY);
                    if (canPortalReplaceBlock(serverLevel, currentPos)) {
                        int startY = currentY;

                        while (currentY > serverLevel.getMinY() && canPortalReplaceBlock(serverLevel, currentPos.move(Direction.DOWN))) {
                            currentY--;
                        }

                        if (currentY + 4 <= maxHeight) {
                            int height = startY - currentY;
                            if (height <= 0 || height >= 3) {
                                currentPos.setY(currentY);
                                if (canHostFrame(serverLevel, currentPos, mutablePos, direction, 0)) {
                                    double distance = startPos.distSqr(currentPos);
                                    if (canHostFrame(serverLevel, currentPos, mutablePos, direction, -1)
                                            && canHostFrame(serverLevel, currentPos, mutablePos, direction, 1)
                                            && (distance1 == -1.0 || distance1 > distance)) {
                                        distance1 = distance;
                                        blockPos2 = currentPos.immutable();
                                    }

                                    if (distance1 == -1.0 && (distance2 == -1.0 || distance2 > distance)) {
                                        distance2 = distance;
                                        blockPos3 = currentPos.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (distance1 == -1.0 && distance2 != -1.0) {
            blockPos2 = blockPos3;
            distance1 = distance2;
        }

        if (distance1 == -1.0) {
            int lowY = Math.max(serverLevel.getMinY() + 1, 70);
            int highY = maxHeight - 9;
            if (highY < lowY) {
                return Optional.empty();
            }

            blockPos2 = new BlockPos(startPos.getX(), Mth.clamp(startPos.getY(), lowY, highY), startPos.getZ()).immutable();
            Direction directionCW = direction.getClockWise();
            if (!worldBorder.isWithinBounds(blockPos2)) {
                return Optional.empty();
            }

            for (int offsetZ = -1; offsetZ < 2; offsetZ++) {
                for (int offsetX = 0; offsetX < getSize().widthWithoutFrame(); offsetX++) {
                    for (int y = -1; y < getSize().heightWithoutFrame(); y++) {
                        mutablePos.setWithOffset(blockPos2, offsetX * direction.getStepX() + offsetZ * directionCW.getStepX(), y, offsetX * direction.getStepZ() + offsetZ * directionCW.getStepZ());
                        if (y < 0) {
                            getFrameBlock().set(serverLevel, mutablePos, Block.UPDATE_ALL);
                        } else {
                            serverLevel.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }

        for (int offsetX = -1; offsetX < getSize().width() - 1; offsetX++) {
            for (int offsetY = -1; offsetY < getSize().height() - 1; offsetY++) {
                if (offsetX == -1 || offsetX == getSize().widthWithoutFrame() || offsetY == -1 || offsetY == getSize().heightWithoutFrame()) {
                    mutablePos.setWithOffset(blockPos2, offsetX * direction.getStepX(), offsetY, offsetX * direction.getStepZ());
                    getFrameBlock().set(serverLevel, mutablePos, Block.UPDATE_ALL);
                }
            }
        }

        BlockEntityPair portalBlock = getPortalBlock();
        if (portalBlock.state().hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            portalBlock = portalBlock.with(portalBlock.state().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis));
        }

        for (int offsetX = 0; offsetX < getSize().widthWithoutFrame(); offsetX++) {
            for (int offsetY = 0; offsetY < getSize().heightWithoutFrame(); offsetY++) {
                mutablePos.setWithOffset(blockPos2, offsetX * direction.getStepX(), offsetY, offsetX * direction.getStepZ());
                portalBlock.set(serverLevel, mutablePos, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
            }
        }

        return Optional.of(new BlockUtil.FoundRectangle(blockPos2.immutable(), 2, 3));
    }

    private static boolean canPortalReplaceBlock(ServerLevel serverLevel, BlockPos.MutableBlockPos mutablePos) {
        BlockState blockState = serverLevel.getBlockState(mutablePos);
        return blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
    }

    private static boolean canHostFrame(ServerLevel serverLevel, BlockPos currentPos, BlockPos.MutableBlockPos mutablePos, Direction direction, int offset) {
        Direction directionCW = direction.getClockWise();

        for (int offsetX = -1; offsetX < 3; offsetX++) {
            for (int offsetY = -1; offsetY < 4; offsetY++) {
                mutablePos.setWithOffset(currentPos, direction.getStepX() * offsetX + directionCW.getStepX() * offset, offsetY, direction.getStepZ() * offsetX + directionCW.getStepZ() * offset);
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
                Codec.INT.fieldOf("width").forGetter(Size::width),
                Codec.INT.fieldOf("height").forGetter(Size::height)
        ).apply(instance, Size::new));

        public int widthWithoutFrame() {
            return width() - 2;
        }

        public int heightWithoutFrame() {
            return height() - 2;
        }
    }
}
