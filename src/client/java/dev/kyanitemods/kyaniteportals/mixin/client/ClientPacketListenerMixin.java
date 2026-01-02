package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.client.PortalOverlayPlayer;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setPortalCooldown(I)V"))
    private void kyanitePortals$setPortalIntensityPacket(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci, @Local(ordinal = 0) LocalPlayer oldPlayer, @Local(ordinal = 1) LocalPlayer newPlayer) {
        ((EntityInPortal) newPlayer).setPortal(((EntityInPortal) oldPlayer).getPortal());
        ((EntityInPortal) newPlayer).setHasTraveled(((EntityInPortal) oldPlayer).hasTraveled());
        ((EntityInPortal) newPlayer).setInsidePortal(((EntityInPortal) oldPlayer).isInsidePortal());
        ((EntityInPortal) newPlayer).setTimeInPortal(((EntityInPortal) oldPlayer).getTimeInPortal());
        ((EntityInPortal) newPlayer).setPortalTeleportTime(((EntityInPortal) oldPlayer).getPortalTeleportTime());
        ((PortalOverlayPlayer) newPlayer).setPortalIntensity(((PortalOverlayPlayer) oldPlayer).getPortalIntensity());
        ((PortalOverlayPlayer) newPlayer).setOldPortalIntensity(((PortalOverlayPlayer) oldPlayer).getOldPortalIntensity());
    }
}
