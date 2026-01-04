package dev.kyanitemods.kyaniteportals.content.testers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class PortalTester<T extends PortalTester<T>> {
    public abstract PortalTesterType<T> getType();
    public abstract PortalTestResult test(Level level, BlockPos pos);
}
