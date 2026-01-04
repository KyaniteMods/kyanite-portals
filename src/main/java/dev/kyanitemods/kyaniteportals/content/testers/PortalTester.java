package dev.kyanitemods.kyaniteportals.content.testers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

public abstract class PortalTester<T extends PortalTester<T>> {
    public abstract PortalTesterType<T> getType();
    public abstract PortalTestResult test(LevelReader level, BlockPos pos);
}
