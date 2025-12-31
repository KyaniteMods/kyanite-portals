package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kyanitemods.kyaniteportals.client.PortalOverlayPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class NauseaMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private float kyanitePortals$applyNausea(float tickDelta, float oldNetherPortalIntensity, float newNetherPortalIntensity, Operation<Float> original) {
        if (((GameRenderer) (Object) this).getMinecraft().player == null) return original.call(tickDelta, oldNetherPortalIntensity, newNetherPortalIntensity);
        float intensity = Mth.lerp(tickDelta, ((PortalOverlayPlayer) ((GameRenderer) (Object) this).getMinecraft().player).getOldPortalIntensity(), ((PortalOverlayPlayer) ((GameRenderer) (Object) this).getMinecraft().player).getPortalIntensity());
        return Math.max(original.call(tickDelta, oldNetherPortalIntensity, newNetherPortalIntensity), intensity);
    }
}
