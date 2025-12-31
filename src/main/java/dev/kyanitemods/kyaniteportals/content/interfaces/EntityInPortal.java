package dev.kyanitemods.kyaniteportals.content.interfaces;

import dev.kyanitemods.kyaniteportals.content.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface EntityInPortal {
    void tick(Level level, BlockPos pos, ResourceKey<Portal> portal);
    void setCooldown(int value);
    int getCooldown();
    void setInsidePortal(boolean value);
    boolean isInsidePortal();
    void setHasTraveled(boolean value);
    boolean hasTraveled();
    void setPortalTeleportTime(int value);
    int getPortalTeleportTime();
    void setTimeInPortal(int value);
    int getTimeInPortal();
    void setPortal(ResourceKey<Portal> portal);
    ResourceKey<Portal> getPortal();
}
