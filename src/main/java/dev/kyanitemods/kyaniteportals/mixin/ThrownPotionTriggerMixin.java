package dev.kyanitemods.kyaniteportals.mixin;

import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.world.entity.player.Player;
//? if <1.21.5 {
/*import net.minecraft.world.entity.projectile.ThrownPotion;
*///? } else if <1.21.11 {
/*import net.minecraft.world.entity.projectile.AbstractThrownPotion;
*///? } else
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(/*? if <1.21.5 {*//*ThrownPotion.class*//*? } else { */AbstractThrownPotion.class/*? }*/)
public class ThrownPotionTriggerMixin {
    //? if <1.21.5 {
    /*@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownPotion;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    *///? } else if <1.21.11 {
    /*@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractThrownPotion;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    *///? } else
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;getItem()Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private void kyanitePortals$triggerThrownPotion(HitResult hitResult, CallbackInfo ci) {
        /*? if <1.21.5 {*//*ThrownPotion*//*? } else { */AbstractThrownPotion/*? }*/ entity = (/*? if <1.21.5 {*//*ThrownPotion*//*? } else { */AbstractThrownPotion/*? }*/) (Object) this;
        PortalTriggers.THROWN_POTION.trigger(entity.level(), entity.blockPosition(), entity.getOwner() instanceof Player player ? player : null, entity);
    }
}
