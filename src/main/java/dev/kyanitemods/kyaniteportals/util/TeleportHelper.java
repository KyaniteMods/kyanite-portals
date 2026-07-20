package dev.kyanitemods.kyaniteportals.util;

//? if <1.21.11 {
/*import net.minecraft.BlockUtil;
*///? } else {
import net.minecraft.util.BlockUtil;
//? }
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
//? if <1.21.2 {
/*import net.minecraft.world.entity.RelativeMovement;
*///? } else
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if <1.21 {
/*import net.minecraft.world.level.portal.PortalInfo;
*///? } else if <1.21.3 {
/*import net.minecraft.world.level.portal.DimensionTransition;
*///? } else
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class TeleportHelper {
    public static /*? if <1.21 {*//*PortalInfo*//*? } else if <1.21.3 { *//*DimensionTransition*//*? } else {*/TeleportTransition/*? }*/ getDimensionTransitionFromExit(Level portalLevel, BlockPos portalPos, ServerLevel exitLevel, BlockUtil.FoundRectangle exitFoundRectangle, @Nullable Direction.Axis entryAxis, Entity entity) {
        BlockState blockState = portalLevel.getBlockState(portalPos);
        Vec3 entryRelativePortalPos;
        if (entryAxis != null) {
            BlockUtil.FoundRectangle entryFoundRectangle = BlockUtil.getLargestRectangleAround(
                    portalPos, entryAxis == Direction.Axis.Y ? Direction.Axis.X : entryAxis, 21, entryAxis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y, 21, blockPos -> portalLevel.getBlockState(blockPos) == blockState
            );
            entryRelativePortalPos = getNetherLikeRelativePosition(entryFoundRectangle, entryAxis, entity.position(), entity.getDimensions(entity.getPose()));
        } else {
            entryRelativePortalPos = new Vec3(0.5, 0.0, 0.0);
        }

        return TeleportHelper.createTeleport(exitLevel, exitFoundRectangle, entryAxis, entryRelativePortalPos, entity);
    }

    public static /*? if <1.21 {*//*PortalInfo*//*? } else if <1.21.3 { *//*DimensionTransition*//*? } else {*/TeleportTransition/*? }*/ createTeleport(ServerLevel exitLevel, BlockUtil.FoundRectangle exitFoundRectangle, Direction.Axis entryAxis, Vec3 entryRelativePortalPos, Entity entity) {
        BlockPos blockPos = exitFoundRectangle.minCorner;
        BlockState blockState = exitLevel.getBlockState(blockPos);
        Direction.Axis exitAxis = blockState.getOptionalValue(BlockStateProperties.AXIS).orElse(blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X));
        double exitPortalWidth = exitFoundRectangle.axis1Size;
        double exitPortalHeight = exitFoundRectangle.axis2Size;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        boolean rotate = entryAxis == exitAxis || (entryAxis != Direction.Axis.Y && exitAxis != Direction.Axis.Y);
        int yaw = rotate ? 0 : 90;
        //? if <1.20.5 {
        /*double entityWidth = entityDimensions.width;
        double entityHeight = entityDimensions.height;
        *///? } else {
        double entityWidth = entityDimensions.width();
        double entityHeight = entityDimensions.height();
        //? }
        double posX = entityWidth / 2.0 + (exitPortalWidth - entityWidth) * entryRelativePortalPos.x();
        double posY = (exitAxis == Direction.Axis.Y ? entityWidth / 2.0 : 0.0) + (exitPortalHeight - (exitAxis == Direction.Axis.Y ? entityWidth : entityHeight)) * entryRelativePortalPos.y();
        double posZ = exitAxis == Direction.Axis.Y ? 0.0 : 0.5 + entryRelativePortalPos.z();

        Vec3 exitPortalPos = switch (exitAxis) {
            case X -> new Vec3((double)blockPos.getX() + posX, (double)blockPos.getY() + posY, (double)blockPos.getZ() + posZ);
            case Y -> new Vec3((double)blockPos.getX() + posX, blockPos.getY() + posZ, (double)blockPos.getZ() + posY);
            case Z -> new Vec3((double)blockPos.getX() + posZ, (double)blockPos.getY() + posY, (double)blockPos.getZ() + posX);
        };
        Vec3 vec33 = PortalShape.findCollisionFreePosition(exitPortalPos, exitLevel, entity, entityDimensions);
        //? if <1.21 {
        /*return new PortalInfo(vec33, rotate ? entity.getDeltaMovement() : new Vec3(entity.getDeltaMovement().z, entity.getDeltaMovement().y, -entity.getDeltaMovement().x), entity.getYRot() + (float)yaw, entity.getXRot());
        *///? } else if <1.21.3 {
        /*return new DimensionTransition(exitLevel, vec33, Vec3.ZERO, yaw, 0.0f, DimensionTransition.DO_NOTHING);
        *///? } else
        return new TeleportTransition(exitLevel, vec33, Vec3.ZERO, yaw, 0.0f, Relative.union(Relative.DELTA, Relative.ROTATION), TeleportTransition.DO_NOTHING);
    }

    public static void teleport(Entity entity, ServerLevel level, /*? if <1.21 {*//*PortalInfo*//*? } else if <1.21.3 { *//*DimensionTransition*//*? } else {*/TeleportTransition/*? }*/ info) {
        //? if <1.21 {
        /*entity.teleportTo(level, info.pos.x(), info.pos.y(), info.pos.z(), java.util.Set.of(), info.yRot, info.xRot);
        entity.setDeltaMovement(info.speed);
        *///? } else if <1.21.3 {
        /*entity.changeDimension(info);
        *///? } else
        entity.teleport(info);
    }

    public static Vec3 getNetherLikeRelativePosition(BlockUtil.FoundRectangle portalFoundRectangle, Direction.Axis portalAxis, Vec3 entityPos, EntityDimensions entityDimensions) {
        Direction.Axis upAxis;
        double x;
        double y;
        //? if <1.20.5 {
        /*double entityWidth = entityDimensions.width;
        double entityHeight = entityDimensions.height;
        *///? } else {
        double entityWidth = entityDimensions.width();
        double entityHeight = entityDimensions.height();
        //? }
        double d = (double) portalFoundRectangle.axis1Size - entityWidth;
        double e = (double) portalFoundRectangle.axis2Size - (portalAxis == Direction.Axis.Y ? entityWidth : entityHeight);
        BlockPos blockPos = portalFoundRectangle.minCorner;
        if (d > 0.0) {
            y = (double)blockPos.get(portalAxis == Direction.Axis.Y ? Direction.Axis.X : portalAxis) + entityWidth / 2.0;
            x = Mth.clamp(Mth.inverseLerp(entityPos.get(portalAxis == Direction.Axis.Y ? Direction.Axis.X : portalAxis) - y, 0.0, d), 0.0, 1.0);
        } else {
            x = 0.5;
        }
        if (e > 0.0) {
            upAxis = portalAxis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
            y = Mth.clamp(Mth.inverseLerp(entityPos.get(upAxis) - (double)blockPos.get(upAxis), 0.0, e), 0.0, 1.0);
        } else {
            y = 0.0;
        }
        Direction.Axis outwardAxis = switch (portalAxis) {
            case X -> Direction.Axis.Z;
            case Y -> Direction.Axis.Y;
            case Z -> Direction.Axis.X;
        };
        double z = entityPos.get(outwardAxis) - ((double)blockPos.get(outwardAxis) + 0.5);
        return new Vec3(x, y, z);
    }
}
