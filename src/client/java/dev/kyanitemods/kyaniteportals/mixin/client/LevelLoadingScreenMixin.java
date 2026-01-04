package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.client.content.effects.LoadingBackgroundOptions;
import dev.kyanitemods.kyaniteportals.client.content.effects.PortalEffects;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {
    //? if >=1.21.9 {
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void kyanitePortals$overrideBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        ResourceKey<Portal> portalKey;
        if (player != null && (portalKey = ((EntityInPortal) player).getPortal()) != null) {
            Optional<? extends LoadingBackgroundOptions<?>> background = PortalEffects.get(portalKey).stream()
                    .filter(options -> options instanceof LoadingBackgroundOptions<?>)
                    .map(options -> (LoadingBackgroundOptions<?>) options)
                    .findFirst();

            if (background.isPresent()) {
                background.get().render((LevelLoadingScreen) (Object) this, guiGraphics, mouseX, mouseY, tickDelta);
                ci.cancel();
            }
        }
    }
    //? }
}
