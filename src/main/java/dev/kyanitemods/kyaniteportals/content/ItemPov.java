package dev.kyanitemods.kyaniteportals.content;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

// This class looks awkward
public final class ItemPov extends Item {
    private ItemPov(Properties properties) {
        super(properties);
    }

    public static BlockHitResult getPlayerHitResult(Level level, Player player, ClipContext.Fluid fluid) {
        return getPlayerPOVHitResult(level, player, fluid);
    }
}
