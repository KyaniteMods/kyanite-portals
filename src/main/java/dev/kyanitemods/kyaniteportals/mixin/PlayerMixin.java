package dev.kyanitemods.kyaniteportals.mixin;

import dev.kyanitemods.kyaniteportals.content.interfaces.GameModeEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements GameModeEntity {
    @Override
    public GameType kyanitePortals$getGameMode() {
        return GameType.DEFAULT_MODE;
    }
}
