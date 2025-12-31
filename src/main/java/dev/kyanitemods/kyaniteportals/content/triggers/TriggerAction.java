package dev.kyanitemods.kyaniteportals.content.triggers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface TriggerAction {
    <I extends PortalTriggerInstance<I>> TriggerResult run(I instance, Level level, BlockPos pos, @Nullable Player player);
}
