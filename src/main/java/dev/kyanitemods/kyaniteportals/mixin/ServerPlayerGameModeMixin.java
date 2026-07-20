package dev.kyanitemods.kyaniteportals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.KyanitePortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;", shift = At.Shift.BY, by = 2), cancellable = true)
    private void kyanitePortals$spectatorEnterPortal(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir, @Local MenuProvider menuProvider, @Local BlockPos pos, @Local BlockState state) {
        if (menuProvider == null && state.getBlock() instanceof KyanitePortalBlock kyanitePortalBlock && kyanitePortalBlock.getPortal(level, pos).map(Portal::spectatorsCanUse).orElse(false)) {
            kyanitePortalBlock.execute(level, pos, serverPlayer, Portal::travelActions);
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
