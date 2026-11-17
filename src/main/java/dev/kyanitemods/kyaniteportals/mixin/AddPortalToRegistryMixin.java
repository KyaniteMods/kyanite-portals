package dev.kyanitemods.kyaniteportals.mixin;

//? if >=1.20.6 {
import net.minecraft.core.RegistrationInfo;
//? }
import com.mojang.serialization.Decoder;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.*;
//? if <26.1 {
import net.minecraft.server.packs.resources.ResourceManager;
import com.mojang.serialization.Lifecycle;
//? }
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

//? if <26.1 {
@Mixin(RegistryDataLoader.class)
//? } else {
/*@Mixin(ResourceManagerRegistryLoadTask.class)
*///? }
public abstract class AddPortalToRegistryMixin {
    //? if <1.20.6 {
    /*@Inject(method = "loadRegistryContents", at = @At("HEAD"))
    private static void kyanitePortals$addPortalsToRegistry(RegistryOps.RegistryInfoLookup registryInfoLookup, ResourceManager resourceManager, ResourceKey<? extends Registry> resourceKey, WritableRegistry writableRegistry, Decoder decoder, Map<ResourceKey<?>, Exception> map, CallbackInfo ci) {
        if (resourceKey == KyanitePortals.RESOURCE_KEY) {
            for (Map.Entry<ResourceKey<Portal>, Function<RegistryOps.RegistryInfoLookup, Portal>> entry : KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet()) {
                writableRegistry.register(entry.getKey(), entry.getValue().apply(registryInfoLookup), Lifecycle.stable());
            }
        }
    }
    *///? } else if <26.1 {
    @Inject(method = "loadContentsFromManager(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;Lnet/minecraft/core/WritableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At("HEAD"))
    private static void kyanitePortals$addPortalsToRegistry(ResourceManager resourceManager, RegistryOps.RegistryInfoLookup registryInfoLookup, WritableRegistry writableRegistry, Decoder decoder, Map<ResourceKey<?>, Exception> map, CallbackInfo ci) {
        if (writableRegistry.key() == KyanitePortals.RESOURCE_KEY) {
            for (Map.Entry<ResourceKey<Portal>, Function<RegistryOps.RegistryInfoLookup, Portal>> entry : KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet()) {
                writableRegistry.register(entry.getKey(), entry.getValue().apply(registryInfoLookup), RegistrationInfo.BUILT_IN);
            }
        }
    }
    //? } else {
    /*@Inject(method = "load", at = @At("HEAD"))
    private void kyanitePortals$addPortalsToRegistry(RegistryOps.RegistryInfoLookup context, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        ResourceManagerRegistryLoadTask task = ((ResourceManagerRegistryLoadTask) (Object) this);
        if (task.registryKey() == KyanitePortals.RESOURCE_KEY) {
            KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet().forEach(entry -> {
                task.registry.register(entry.getKey(), entry.getValue().apply(context), RegistrationInfo.BUILT_IN);
            });
        }
    }

//    @Inject(method = "registerElements", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
//    private void kyanitePortals$addPortalsToRegistry(Stream<RegistryLoadTask.PendingRegistration> elements, CallbackInfo ci) {
//        RegistryLoadTask task = ((RegistryLoadTask) (Object) this);
//        if (task.registryKey() == KyanitePortals.RESOURCE_KEY) {
//            KyanitePortals.PORTAL_REGISTRY_OVERRIDES.entrySet().forEach(entry -> {
//                task.registry.register(entry.getKey(), entry.getValue().apply(task.data), RegistrationInfo.BUILT_IN);
//            });
//        }
//    }
    *///? }
}
