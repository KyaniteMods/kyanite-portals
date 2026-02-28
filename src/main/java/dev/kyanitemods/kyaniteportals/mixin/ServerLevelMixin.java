package dev.kyanitemods.kyaniteportals.mixin;

import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "levelEvent", at = @At("HEAD"))
    //? if <1.21.5 {
    //private void kyanitePortals$levelEventTrigger(net.minecraft.world.entity.Entity entity, int type, BlockPos pos, int data, CallbackInfo ci) {
    //? } else
    private void kyanitePortals$levelEventTrigger(net.minecraft.world.entity.player.Player entity, int type, BlockPos pos, int data, CallbackInfo ci) {
        PortalTriggers.LEVEL_EVENT.trigger((ServerLevel) (Object) this, type, pos, data);
    }
}
