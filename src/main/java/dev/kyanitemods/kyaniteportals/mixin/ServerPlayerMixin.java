package dev.kyanitemods.kyaniteportals.mixin;

import dev.kyanitemods.kyaniteportals.content.interfaces.GameModeEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements GameModeEntity {
    @Override
    public GameType kyanitePortals$getGameMode() {
        return ((ServerPlayer) (Object) this).gameMode.getGameModeForPlayer();
    }
}
