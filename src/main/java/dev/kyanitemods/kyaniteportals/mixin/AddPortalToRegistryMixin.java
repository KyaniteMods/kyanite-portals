package dev.kyanitemods.kyaniteportals.mixin;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
//? if >=1.20.6
//import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@Mixin(RegistryDataLoader.class)
public class AddPortalToRegistryMixin {
    //? if <1.20.6 {
    @Inject(method = "loadRegistryContents", at = @At("HEAD"))
    private static void kyanitePortals$addPortalsToRegistry(RegistryOps.RegistryInfoLookup registryInfoLookup, ResourceManager resourceManager, ResourceKey<? extends Registry> resourceKey, WritableRegistry writableRegistry, Decoder decoder, Map<ResourceKey<?>, Exception> map, CallbackInfo ci) {
        if (resourceKey == KyanitePortals.RESOURCE_KEY) {
            for (Map.Entry<ResourceKey<Portal>, Function<RegistryOps.RegistryInfoLookup, Portal>> entry : KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet()) {
                writableRegistry.register(entry.getKey(), entry.getValue().apply(registryInfoLookup), Lifecycle.stable());
            }
        }
    }
    //? } else {
    /*@Inject(method = "Lnet/minecraft/resources/RegistryDataLoader;loadContentsFromManager(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;Lnet/minecraft/core/WritableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At("HEAD"))
    private static void kyanitePortals$addPortalsToRegistry(ResourceManager resourceManager, RegistryOps.RegistryInfoLookup registryInfoLookup, WritableRegistry writableRegistry, Decoder decoder, Map<ResourceKey<?>, Exception> map, CallbackInfo ci) {
        if (writableRegistry.key() == KyanitePortals.RESOURCE_KEY) {
            for (Map.Entry<ResourceKey<Portal>, Function<RegistryOps.RegistryInfoLookup, Portal>> entry : KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet()) {
                writableRegistry.register(entry.getKey(), entry.getValue(), RegistrationInfo.BUILT_IN);
            }
        }
    }*/
    //? }
}
