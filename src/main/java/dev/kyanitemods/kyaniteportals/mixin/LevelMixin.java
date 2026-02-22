package dev.kyanitemods.kyaniteportals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelMixin {
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V", shift = At.Shift.AFTER))
    private void kyanitePortals$blockChangeTrigger(CallbackInfoReturnable<BlockState> cir, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) BlockState blockState) {
        PortalTriggers.BLOCK_CHANGE.trigger(((LevelChunk) (Object) this).getLevel(), blockPos, null, blockState);
    }
}
