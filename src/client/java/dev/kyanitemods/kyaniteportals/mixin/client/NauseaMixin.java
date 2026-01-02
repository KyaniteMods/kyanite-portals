package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.client.PortalOverlayPlayer;
import dev.kyanitemods.kyaniteportals.client.content.effects.PortalEffects;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class NauseaMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private float kyanitePortals$applyNausea(float tickDelta, float oldNetherPortalIntensity, float newNetherPortalIntensity, Operation<Float> original) {
        LocalPlayer player = ((GameRenderer) (Object) this).getMinecraft().player;
        if (player == null || ((EntityInPortal) player).getPortal() == null || PortalEffects.get(((EntityInPortal) player).getPortal(), PortalEffects.NAUSEA).isEmpty()) return original.call(tickDelta, oldNetherPortalIntensity, newNetherPortalIntensity);
        float intensity = Mth.lerp(tickDelta, ((PortalOverlayPlayer) player).getOldPortalIntensity(), ((PortalOverlayPlayer) player).getPortalIntensity());
        return intensity == 0.0f ? original.call(tickDelta, oldNetherPortalIntensity, newNetherPortalIntensity) : Math.max(original.call(tickDelta, oldNetherPortalIntensity, newNetherPortalIntensity), intensity);
    }

    //? if >=1.21.5 {
    @Definition(id = "localPlayer", local = @Local(type = LocalPlayer.class))
    @Definition(id = "portalEffectIntensity", field = "Lnet/minecraft/client/player/LocalPlayer;portalEffectIntensity:F")
    @Expression("localPlayer.portalEffectIntensity")
    @WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float kyanitePortals$applyNauseaSpeed(LocalPlayer instance, Operation<Float> original) {
        LocalPlayer player = ((GameRenderer) (Object) this).getMinecraft().player;
        if (player == null || ((EntityInPortal) player).getPortal() == null || PortalEffects.get(((EntityInPortal) player).getPortal(), PortalEffects.NAUSEA).isEmpty()) return original.call(instance);
        float intensity = ((PortalOverlayPlayer) player).getPortalIntensity();
        return intensity == 0.0f ? original.call(instance) : Math.max(original.call(instance), intensity);
    }
    //? }
}
