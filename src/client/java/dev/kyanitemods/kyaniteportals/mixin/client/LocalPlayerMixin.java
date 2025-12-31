package dev.kyanitemods.kyaniteportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kyanitemods.kyaniteportals.client.PortalOverlayPlayer;
import dev.kyanitemods.kyaniteportals.client.content.effects.PortalEffects;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;
//? if >=1.21
//import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements PortalOverlayPlayer {
    @Unique
    private float oldPortalIntensity;

    @Unique
    private float portalIntensity;

    @Override
    public float getOldPortalIntensity() {
        return oldPortalIntensity;
    }

    @Override
    public float getPortalIntensity() {
        return portalIntensity;
    }

    @Override
    public void setOldPortalIntensity(float value) {
        oldPortalIntensity = value;
    }

    @Override
    public void setPortalIntensity(float value) {
        portalIntensity = value;
    }

    @Mixin(LocalPlayer.class)
    private interface DistortionIntensityAccessor {
        @Accessor(/*? if <1.21.5 {*/"oSpinningEffectIntensity"/*? } else*//*"oPortalEffectIntensity"*/)
        float getOldDistortionIntensity();

        @Accessor(/*? if <1.21.5 {*/"spinningEffectIntensity"/*? } else*//*"portalEffectIntensity"*/)
        float getDistortionIntensity();

        @Accessor(/*? if <1.21.5 {*/"oSpinningEffectIntensity"/*? } else*//*"oPortalEffectIntensity"*/)
        void setOldDistortionIntensity(float value);

        @Accessor(/*? if <1.21.5 {*/"spinningEffectIntensity"/*? } else*//*"portalEffectIntensity"*/)
        void setDistortionIntensity(float value);
    }

    @Mixin(LocalPlayer.class)
    private interface MinecraftAccessor {
        @Accessor("minecraft")
        Minecraft getMinecraft();
    }

    @Mixin(Entity.class)
    private interface PortalCooldownAccessor {
        @Invoker("processPortalCooldown")
        void invokeProcessPortalCooldown();
    }

    @Inject(method = /*? if <1.21 {*/"handleNetherPortalClient"/*? } else if <1.21.5 { *//*"Lnet/minecraft/client/player/LocalPlayer;handleConfusionTransitionEffect()V"*//*? } else*//*"Lnet/minecraft/client/player/LocalPlayer;handlePortalTransitionEffect(Z)V"*/, at = @At("TAIL"))
    private void kyanitePortals$handlePortalClient(CallbackInfo ci) {

    }

    //? if <1.21 {
    @Inject(method = "handleNetherPortalClient", at = @At(value = "HEAD"), cancellable = true)
    private void kyanitePortals$setDistortionIntensity(CallbackInfo ci) {
    //? } else if <1.21.5 {
    /*@Inject(method = "Lnet/minecraft/client/player/LocalPlayer;handleConfusionTransitionEffect()V", at = @At("HEAD"), cancellable = true)
    private void kyanitePortals$setDistortionIntensity(CallbackInfo ci) {*/
    //? } else {
    /*@Inject(method = "Lnet/minecraft/client/player/LocalPlayer;handlePortalTransitionEffect(Z)V", at = @At("HEAD"), cancellable = true)
    private void kyanitePortals$setDistortionIntensity(boolean effect, CallbackInfo ci) {*/
    //? }
        setOldPortalIntensity(getPortalIntensity());

        if (((EntityInPortal) this).isInsidePortal() && !((EntityInPortal) this).hasTraveled()) {
            setPortalIntensity(Mth.clamp(getPortalIntensity() + 0.0125f, 0.0f, 1.0f));
        } else if (getPortalIntensity() > 0.0f) {
            setPortalIntensity(Mth.clamp(getPortalIntensity() - 0.05f, 0.0f, 1.0f));
        }

        if (((EntityInPortal) this).isInsidePortal()) {
            if (((EntityInPortal) this).getPortal() != null) {
                if (PortalEffects.get(((EntityInPortal) this).getPortal(), PortalEffects.CLOSE_SCREENS).isPresent()) {
                    if (((MinecraftAccessor) this).getMinecraft().screen != null && /*? if <1.21.9 {*/!((MinecraftAccessor) this).getMinecraft().screen.isPauseScreen() && !(((MinecraftAccessor) this).getMinecraft().screen instanceof DeathScreen) && !(((MinecraftAccessor) this).getMinecraft().screen instanceof WinScreen) /*? } else *//*!((MinecraftAccessor) this).getMinecraft().screen.isAllowedInPortal()*/) {
                        if (((MinecraftAccessor) this).getMinecraft().screen instanceof AbstractContainerScreen) {
                            ((LocalPlayer) (Object) this).closeContainer();
                        }
                        ((MinecraftAccessor) this).getMinecraft().setScreen(null);
                    }
                }
            }
            ((EntityInPortal) this).setInsidePortal(false);
        }
    }

    @WrapOperation(method = /*? if <1.21 {*/"handleNetherPortalClient"/*? } else if <1.21.5 { *//*"Lnet/minecraft/client/player/LocalPlayer;handleConfusionTransitionEffect()V"*//*? } else*//*"Lnet/minecraft/client/player/LocalPlayer;handlePortalTransitionEffect(Z)V"*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void kyanitePortals$doNotPlayPortalSound(SoundManager instance, SoundInstance soundInstance, Operation<Void> original) {
        if (((EntityInPortal) this).isInsidePortal() && !((EntityInPortal) this).hasTraveled()
                && ((EntityInPortal) this).getPortal() != null
                && PortalEffects.get(((EntityInPortal) this).getPortal(), PortalEffects.NAUSEA).isPresent()) {
            return;
        }
        original.call(instance, soundInstance);
    }
}
