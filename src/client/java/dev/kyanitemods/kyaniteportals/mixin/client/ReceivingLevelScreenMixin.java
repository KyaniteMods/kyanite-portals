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
//? if <1.20.9
//import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Pseudo
@Mixin(targets = "net.minecraft.client.gui.screens.ReceivingLevelScreen")
public class ReceivingLevelScreenMixin {
    //? if <1.20.2 {
    /*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ReceivingLevelScreen;renderDirtBackground(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void kyanitePortals$overrideBackground(ReceivingLevelScreen instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY, @Local(argsOnly = true) float tickDelta) {
        Player player = Minecraft.getInstance().player;
        ResourceKey<Portal> portalKey;
        if (player != null && ((EntityInPortal) player).isInsidePortal() && (portalKey = ((EntityInPortal) player).getPortal()) != null) {
            Optional<? extends LoadingBackgroundOptions<?>> background = PortalEffects.get(portalKey).stream()
                    .filter(options -> options instanceof LoadingBackgroundOptions<?>)
                    .map(options -> (LoadingBackgroundOptions<?>) options)
                    .findFirst();

            if (background.isPresent()) {
                background.get().render(instance, guiGraphics, mouseX, mouseY, tickDelta);
                return;
            }
        }
        original.call(instance, guiGraphics);
    }
    *///? }

    //? if >=1.20.2 <1.21.9 {
    /*@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void kyanitePortals$overrideBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        ResourceKey<Portal> portalKey;
        if (player != null && ((EntityInPortal) player).isInsidePortal() && (portalKey = ((EntityInPortal) player).getPortal()) != null) {
            Optional<? extends LoadingBackgroundOptions<?>> background = PortalEffects.get(portalKey).stream()
                    .filter(options -> options instanceof LoadingBackgroundOptions<?>)
                    .map(options -> (LoadingBackgroundOptions<?>) options)
                    .findFirst();

            if (background.isPresent()) {
                background.get().render((ReceivingLevelScreen) (Object) this, guiGraphics, mouseX, mouseY, tickDelta);
                ci.cancel();
            }
        }
    }
    *///? }
}
