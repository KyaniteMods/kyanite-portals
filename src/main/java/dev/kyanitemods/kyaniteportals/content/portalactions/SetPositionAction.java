package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
import com.mojang.serialization.Codec;
//? } else
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
//? if <1.21.2 {
import net.minecraft.world.entity.RelativeMovement;
//? } else
//import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.3 {
//import net.minecraft.world.level.portal.TeleportTransition;
//? } else if >=1.21
//import net.minecraft.world.level.portal.DimensionTransition;

import java.util.Optional;
import java.util.Set;

public final class SetPositionAction extends PortalAction<SetPositionAction> {
    //$ map_codec_swap SetPositionAction
    public static final Codec<SetPositionAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Settings.REQUIRED_LOCATION_CODEC.fieldOf("settings").forGetter(SetPositionAction::getSettings),
            FloatProvider.CODEC.optionalFieldOf("yaw").forGetter(SetPositionAction::getYRot),
            FloatProvider.CODEC.optionalFieldOf("pitch").forGetter(SetPositionAction::getXRot)
    ).apply(instance, SetPositionAction::new));

    private final Optional<FloatProvider> yRot;
    private final Optional<FloatProvider> xRot;

    public SetPositionAction(Settings settings, Optional<FloatProvider> yRot, Optional<FloatProvider> xRot) {
        super(settings);
        this.yRot = yRot;
        this.xRot = xRot;
    }

    @Override
    public PortalActionType<SetPositionAction> getType() {
        return PortalActions.SET_POSITION;
    }

    public Optional<FloatProvider> getYRot() {
        return yRot;
    }

    public Optional<FloatProvider> getXRot() {
        return xRot;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        if (entity == null || level.isClientSide()) return PortalActionResult.FAILURE;

        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);

        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty()) return PortalActionResult.FAILURE;
        final ServerLevel serverLevel = optional.get();

        float yRot = getYRot().map(provider -> provider.sample(level.getRandom())).orElse(entity.getYRot());
        float xRot = getXRot().map(provider -> provider.sample(level.getRandom())).orElse(entity.getXRot());
        //? if <1.21 {
        entity.teleportTo(serverLevel, location.position().x(), location.position().y(), location.position().z(), Set.of(), yRot, xRot);
        //? } else if <1.21.3 {
        //entity.changeDimension(new DimensionTransition(serverLevel, location.position(), entity.getDeltaMovement(), yRot, xRot, DimensionTransition.DO_NOTHING));
        //? } else
        //entity.teleport(new TeleportTransition(serverLevel, location.position(), entity.getDeltaMovement(), yRot, xRot, Relative.union(Relative.DELTA, Relative.ROTATION), TeleportTransition.DO_NOTHING));
        return PortalActionResult.SUCCESS;
    }
}
