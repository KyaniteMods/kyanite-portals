package dev.kyanitemods.kyaniteportals.content.generators;

import dev.kyanitemods.kyaniteportals.content.testers.PortalTester;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import org.jetbrains.annotations.Nullable;

public record GeneratorContext(@Nullable PortalTriggerInstance<?> trigger, @Nullable PortalTester<?> tester, ServerLevel level, BlockPos pos, @Nullable Player player) {
}
