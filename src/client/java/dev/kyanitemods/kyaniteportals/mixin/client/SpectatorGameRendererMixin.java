package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.KyanitePortalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class SpectatorGameRendererMixin {
    @Definition(id = "getMenuProvider", method = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;")
    @Definition(id = "blockState", local = @Local(type = BlockState.class))
    @Expression("blockState.getMenuProvider(?, ?) != null")
    @WrapOperation(method = "shouldRenderBlockOutline", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean kyanitePortals$renderBlockOutline(Object left, Object right, Operation<Boolean> original, @Local BlockPos pos) {
        if (original.call(left, right)) return true;
        Level level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof KyanitePortalBlock block && block.getPortal(level, pos).map(Portal::spectatorsCanUse).orElse(false);
    }
}
