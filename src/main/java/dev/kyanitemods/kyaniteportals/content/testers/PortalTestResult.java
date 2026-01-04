package dev.kyanitemods.kyaniteportals.content.testers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

import java.util.function.BiConsumer;

public interface PortalTestResult {
    boolean isSuccess();
    void placePortalBlocks(LevelAccessor level, BiConsumer<LevelAccessor, BlockPos> placer);
    Direction.Axis getAxis();
    int getPortalBlocks();
    default boolean isEmpty() {
        return getPortalBlocks() == 0;
    }
    boolean isComplete();
}
