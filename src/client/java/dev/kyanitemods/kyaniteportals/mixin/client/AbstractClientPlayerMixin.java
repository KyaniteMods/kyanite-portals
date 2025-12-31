package dev.kyanitemods.kyaniteportals.mixin.client;

import dev.kyanitemods.kyaniteportals.content.interfaces.GameModeEntity;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin implements GameModeEntity {
    @Shadow
    @Nullable
    protected abstract PlayerInfo getPlayerInfo();

    @Override
    public GameType kyanitePortals$getGameMode() {
        PlayerInfo info = getPlayerInfo();
        if (info == null) return GameType.DEFAULT_MODE;
        return info.getGameMode();
    }
}
