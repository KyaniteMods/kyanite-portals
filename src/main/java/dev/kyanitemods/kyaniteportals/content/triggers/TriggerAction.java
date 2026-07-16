package dev.kyanitemods.kyaniteportals.content.triggers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import org.jetbrains.annotations.Nullable;

public interface TriggerAction {
    <I extends PortalTriggerInstance<I>> TriggerResult run(I instance, ServerLevel level, BlockPos pos, @Nullable Player player);
}
