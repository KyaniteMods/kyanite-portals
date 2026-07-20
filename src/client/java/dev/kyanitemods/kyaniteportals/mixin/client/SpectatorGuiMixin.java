package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.KyanitePortalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class SpectatorGuiMixin {
    @ModifyReturnValue(method = "canRenderCrosshairForSpectator", at = @At(value = "RETURN", ordinal = 2))
    private boolean kyanitePortals$renderCrosshairWhenPortal(boolean original, @Local(argsOnly = true) HitResult hitResult) {
        if (original) return true;
        BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
        Level level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof KyanitePortalBlock block && block.getPortal(level, pos).map(Portal::spectatorsCanUse).orElse(false);
    }
}
