package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStatePredictionHandler.class)
public class BlockPredictionFixMixin {
    @Mixin(BlockStatePredictionHandler.ServerVerifiedState.class)
    private interface ServerVerifiedStateAccessor {
        @Accessor("blockState")
        BlockState getBlockState();
    }

    @Unique
    private BlockState previousBlock = null;

    @Inject(method = "updateKnownServerState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/prediction/BlockStatePredictionHandler$ServerVerifiedState;setBlockState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void kyanitePortals$setPreviousPredictedBlock(BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<Boolean> cir, @Local BlockStatePredictionHandler.ServerVerifiedState serverVerifiedState) {
        previousBlock = ((ServerVerifiedStateAccessor) serverVerifiedState).getBlockState();
    }

    @ModifyReturnValue(method = "updateKnownServerState", at = @At(value = "RETURN", ordinal = 1))
    private boolean kyanitePortals$fixBlockPrediction(boolean original, @Local(argsOnly = true) BlockState state) {
        if (previousBlock != null && previousBlock.getBlock() != state.getBlock()) return false;
        return original;
    }
}
