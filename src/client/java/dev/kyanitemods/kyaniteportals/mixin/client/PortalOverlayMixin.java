package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kyanitemods.kyaniteportals.client.PortalOverlayPlayer;
import dev.kyanitemods.kyaniteportals.client.content.effects.PortalEffects;
import dev.kyanitemods.kyaniteportals.client.content.effects.TextureOverlayPortalEffectOptions;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
//? if >=1.21
import net.minecraft.client.DeltaTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.21.5
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Gui.class)
public class PortalOverlayMixin {
    @Mixin(Gui.class)
    private interface GuiAccessor {
        @Accessor("minecraft")
        Minecraft getMinecraft();
    }

    @Mixin(Entity.class)
    private interface EntityAccessor {
        //? if <1.21 {
        /*@Accessor("isInsidePortal")
        boolean getIsInsidePortal();
        *///? }
    }

    @WrapOperation(method = /*? if <1.20.6 {*//*"render"*//*? } else {*/"renderCameraOverlays"/*? }*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPortalOverlay(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    private void kyanitePortals$checkPortalOverlay(Gui instance, GuiGraphics guiGraphics, float f, Operation<Void> original, @Local(argsOnly = true) /*? if <1.21 {*//*float tickDelta*//*? } else {*/DeltaTracker deltaTracker/*? }*/) {
        //? if >=1.21
        float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);
        Player player = ((GuiAccessor) this).getMinecraft().player;
        if (player == null) {
            original.call(instance, guiGraphics, f);
            return;
        }
        float lerp = Mth.lerp(tickDelta, ((PortalOverlayPlayer) player).getOldPortalIntensity(), ((PortalOverlayPlayer) player).getPortalIntensity());
        //? if <1.21 {
        /*if (lerp == 0.0f && (((EntityAccessor) player).getIsInsidePortal() || player.isOnPortalCooldown())) original.call(instance, guiGraphics, f);
        *///? } else {
        if (lerp == 0.0f && player.portalProcess != null && (player.portalProcess.isInsidePortalThisTick() || player.isOnPortalCooldown())) original.call(instance, guiGraphics, f);
        //? }
    }

    @Inject(method = /*? if <1.20.6 {*//*"render"*//*? } else {*/"renderCameraOverlays"/*? }*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 1))
    private void kyanitePortals$renderPortalOverlay(GuiGraphics guiGraphics, /*? if <1.21 {*//*float tickDelta*//*? } else {*/DeltaTracker deltaTracker/*? }*/, CallbackInfo ci) {
        //? if >=1.21
        float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);
        Player player = ((GuiAccessor) this).getMinecraft().player;
        float lerp = Mth.lerp(tickDelta, ((PortalOverlayPlayer) player).getOldPortalIntensity(), ((PortalOverlayPlayer) player).getPortalIntensity());
        if (lerp > 0.0f) {
            Optional<TextureOverlayPortalEffectOptions> optional = PortalEffects.get(((EntityInPortal) player).getPortal(), PortalEffects.TEXTURE_OVERLAY);
            optional.ifPresent(options -> renderPortalOverlay(guiGraphics, options.getAtlas(), options.getTexture(), options.getTint(), lerp));
        }
    }

    @Unique
    private void renderPortalOverlay(GuiGraphics guiGraphics, Identifier atlas, Identifier id, int tint, float f) {
        if (f < 1.0f) {
            f *= f;
            f *= f;
            f = f * 0.8f + 0.2f;
        }

        //? if <1.21.9 {
        /*TextureAtlasSprite sprite = ((GuiAccessor) this).getMinecraft().getTextureAtlas(atlas).apply(id);
        *///? } else
        TextureAtlasSprite sprite = ((GuiAccessor) this).getMinecraft().getAtlasManager().getAtlasOrThrow(atlas).getSprite(id);

        //? if <1.21.5 {
        /*RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        //? if >=1.20.6
        RenderSystem.enableBlend();
        float red = ((tint >> 16) & 0xFF) / 255.0f;
        float green = ((tint >> 8) & 0xFF) / 255.0f;
        float blue = (tint & 0xFF) / 255.0f;
        guiGraphics.setColor(red, green, blue, f);
        guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), sprite);
        //? if >=1.20.6
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        *///? } else {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), (tint & (0x00FFFFFF)) | (((int) (f * 255.0f)) << 24));
        //? }
    }
}
