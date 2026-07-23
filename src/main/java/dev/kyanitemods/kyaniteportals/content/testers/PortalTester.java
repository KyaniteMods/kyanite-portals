package dev.kyanitemods.kyaniteportals.content.testers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;

public abstract class PortalTester<T extends PortalTester<T>> {
    public abstract PortalTesterType<T> getType();
    public abstract PortalTestResult test(Level level, BlockPos pos);
}
