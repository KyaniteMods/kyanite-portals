package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.mixin.EntityAccessor;
import dev.kyanitemods.kyaniteportals.mixin.ServerPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
//? if <1.21 {
/*import net.minecraft.world.level.portal.PortalInfo;
*///? } else if <1.21.3 {

/*import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.DimensionTransition;
 
*///? } else {

import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.entity.Relative;
 
//? }
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class TeleportToServerSpawnPointAction extends PortalAction<TeleportToServerSpawnPointAction> {
    //$ map_codec_swap TeleportToServerSpawnPointAction
    public static final MapCodec<TeleportToServerSpawnPointAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation()
    ).apply(instance, TeleportToServerSpawnPointAction::new));

    public TeleportToServerSpawnPointAction(Settings settings) {
        super(settings);
    }

    @Override
    public PortalActionType<TeleportToServerSpawnPointAction> getType() {
        return PortalActions.TELEPORT_TO_SERVER_SPAWN_POINT;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        if (entity == null) return PortalActionResult.FAILURE;
        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);
        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty()) return PortalActionResult.FAILURE;
        ServerLevel serverLevel = optional.get();
        //? if <1.21 {
        /*PortalInfo info = ((EntityAccessor) entity).callFindDimensionEntryPoint(serverLevel);

        if (info == null) return PortalActionResult.FAILURE;
        entity.teleportTo(serverLevel, info.pos.x(), info.pos.y(), info.pos.z(), Set.of(), info.yRot, info.xRot);
        *///? } else if <1.21.3 {
        /*BlockPos blockPos = serverLevel.getSharedSpawnPos();
        Vec3 vec3 = blockPos.getBottomCenter();
        float f = entity.getYRot();
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            serverPlayer.changeDimension(serverPlayer.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING));
            return PortalActionResult.SUCCESS;
        }
        vec3 = entity.adjustSpawnLocation(serverLevel, blockPos).getBottomCenter();
        entity.changeDimension(new DimensionTransition(serverLevel, vec3, entity.getDeltaMovement(), f, entity.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)));
        *///? } else {
        if (entity instanceof ServerPlayer player) {
            player.teleport(player.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING));
            return PortalActionResult.SUCCESS;
        }
        LevelData.RespawnData respawnData = serverLevel.getRespawnData();
        ResourceKey<Level> resourceKey = respawnData.dimension();
        BlockPos blockPos = respawnData.pos();
        Vec3 vec3 = blockPos.getBottomCenter();

        float f = respawnData.yaw();
        float g = respawnData.pitch();
        Set<Relative> set = Relative.union(Relative.DELTA, Relative.ROTATION);
        vec3 = entity.adjustSpawnLocation(serverLevel, blockPos).getBottomCenter();
        entity.teleport(new TeleportTransition(serverLevel, vec3, Vec3.ZERO, f, g, set, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)));
        //? }
        return PortalActionResult.SUCCESS;
    }
}
