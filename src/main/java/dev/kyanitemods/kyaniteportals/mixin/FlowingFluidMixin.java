package dev.kyanitemods.kyaniteportals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    //? if >1.21.3 {
    @ModifyReturnValue(method = "canHoldAnyFluid", at = @At("RETURN"))
    private static boolean kyanitePortals$portalCanHoldFluid(boolean original, @Local(argsOnly = true) BlockState state) {
        return original && !state.is(KyanitePortalsBlocks.CUSTOM_PORTAL);
    }
    //? } else {
    /*@WrapOperation(method = "canHoldFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 5))
    private boolean kyanitePortals$portalCanHoldFluid(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.is(KyanitePortalsBlocks.CUSTOM_PORTAL);
    }
    *///? }
}
