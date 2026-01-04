package dev.kyanitemods.kyaniteportals.mixin;

import net.minecraft.core.Direction;
//? if <1.21.11 {
/*import net.minecraft.BlockUtil;
*///? } else
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.Entity;
//? if <1.21 {
/*import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.server.level.ServerLevel;
*///? }
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    //? if <1.21 {
    /*@Invoker("findDimensionEntryPoint")
    PortalInfo callFindDimensionEntryPoint(ServerLevel level);
    *///? }

    @Invoker("getRelativePortalPosition")
    Vec3 callGetRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle);
}
