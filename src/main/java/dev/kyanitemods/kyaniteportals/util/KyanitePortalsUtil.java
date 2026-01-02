package dev.kyanitemods.kyaniteportals.util;

//? if <1.21.11 {
/*import net.minecraft.BlockUtil;
*///? } else
import net.minecraft.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
//? if <1.21.2 {
/*import net.minecraft.world.entity.RelativeMovement;
*///? } else
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if <1.21 {
/*import net.minecraft.world.level.portal.PortalInfo;
*///? } else if <1.21.3 {
//import net.minecraft.world.level.portal.DimensionTransition;
//? } else
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public class KyanitePortalsUtil {
    public static Identifier getIdentifier(ResourceKey<?> resourceKey) {
        //? if <1.21.11 {
        /*return resourceKey.location();
        *///? } else
        return resourceKey.identifier();
    }

    @ApiStatus.Internal
    public static /*? if <1.21 {*//*PortalInfo*//*? } else if <1.21.3 { *//*DimensionTransition*//*? } else */TeleportTransition createTeleport(ServerLevel serverLevel, BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, Entity entity) {
        BlockPos blockPos = foundRectangle.minCorner;
        BlockState blockState = serverLevel.getBlockState(blockPos);
        Direction.Axis axis2 = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d = foundRectangle.axis1Size;
        double e = foundRectangle.axis2Size;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        int i = axis == axis2 ? 0 : 90;
        //? if <1.20.5 {
        /*double f = (double)entityDimensions.width / 2.0 + (d - (double)entityDimensions.width) * vec3.x();
        double g = (e - (double)entityDimensions.height) * vec3.y();
        *///? } else {
        
        double f = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * vec3.x();
        double g = (e - (double)entityDimensions.height()) * vec3.y();
         
        //? }
        double h = 0.5 + vec3.z();
        boolean bl = axis2 == Direction.Axis.X;
        Vec3 vec32 = new Vec3((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
        Vec3 vec33 = PortalShape.findCollisionFreePosition(vec32, serverLevel, entity, entityDimensions);
        //? if <1.21 {
        /*return new PortalInfo(vec33, axis == axis2 ? entity.getDeltaMovement() : new Vec3(entity.getDeltaMovement().z, entity.getDeltaMovement().y, -entity.getDeltaMovement().x), entity.getYRot() + (float)i, entity.getXRot());
        *///? } else if <1.21.3 {
        //return new DimensionTransition(serverLevel, vec33, Vec3.ZERO, i, 0.0f, DimensionTransition.DO_NOTHING);
        //? } else
        return new TeleportTransition(serverLevel, vec33, Vec3.ZERO, i, 0.0f, Relative.union(Relative.DELTA, Relative.ROTATION), TeleportTransition.DO_NOTHING);
    }

    public static void teleport(Entity entity, ServerLevel level, /*? if <1.21 {*//*PortalInfo*//*? } else if <1.21.3 { *//*DimensionTransition*//*? } else */TeleportTransition info) {
        //? if <1.21 {
        /*entity.teleportTo(level, info.pos.x(), info.pos.y(), info.pos.z(), Set.of(), info.yRot, info.xRot);
        entity.setDeltaMovement(info.speed);
        *///? } else if <1.21.3 {
        //entity.changeDimension(info);
        //? } else
        entity.teleport(info);
    }
}
