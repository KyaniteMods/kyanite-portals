package dev.kyanitemods.kyaniteportals.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
//? if <1.21
import net.minecraft.world.level.portal.PortalInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    //? if <1.21 {
    @Invoker("findDimensionEntryPoint")
    PortalInfo callFindDimensionEntryPoint(ServerLevel level);
    //? }
}
