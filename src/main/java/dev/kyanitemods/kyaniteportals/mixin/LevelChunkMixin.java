package dev.kyanitemods.kyaniteportals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Inject(method = "setBlockState", at = @At(value = "RETURN", ordinal = 3))
    private void kyanitePortals$blockChangeTrigger(CallbackInfoReturnable<BlockState> cir, @Local(argsOnly = true) BlockPos blockPos) {
        Level level = ((LevelChunk) (Object) this).getLevel();
        PortalTriggers.BLOCK_CHANGE.trigger(level, blockPos, null);
    }
}
