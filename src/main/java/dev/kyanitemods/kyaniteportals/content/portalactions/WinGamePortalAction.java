package dev.kyanitemods.kyaniteportals.content.portalactions;

import com.mojang.serialization.Codec;
//? if >=1.20.6
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.mixin.ServerPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WinGamePortalAction extends PortalAction<WinGamePortalAction> {
    //$ map_codec_swap WinGamePortalAction
    public static final Codec<WinGamePortalAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Settings.optionalLocation(),
            Codec.BOOL.optionalFieldOf("show_credits").forGetter(WinGamePortalAction::shouldShowCredits)
    ).apply(instance, WinGamePortalAction::new));

    private final Optional<Boolean> showCredits;

    public WinGamePortalAction(Settings settings, Optional<Boolean> showCredits) {
        super(settings);
        this.showCredits = showCredits;
    }

    @Override
    public PortalActionType<WinGamePortalAction> getType() {
        return PortalActions.WIN_GAME;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) return PortalActionResult.FAILURE;
        if (player.wonGame) return PortalActionResult.FAILURE;

        player.unRide();
        ((ServerLevel) level).removePlayerImmediately(player, Entity.RemovalReason.CHANGED_DIMENSION);

        player.wonGame = true;
        boolean showCredits = (shouldShowCredits().isEmpty() && !((ServerPlayerAccessor) player).hasSeenCredits()) || shouldShowCredits().orElse(false);
        if (showCredits) {
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 1.0F));
            ((ServerPlayerAccessor) player).setSeenCredits(true);
        } else {
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0.0F));
        }
        return PortalActionResult.SUCCESS;
    }

    public Optional<Boolean> shouldShowCredits() {
        return showCredits;
    }
}
