package dev.kyanitemods.kyaniteportals.client;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.client.content.effects.PortalEffectManager;
import dev.kyanitemods.kyaniteportals.client.content.particles.CustomPortalParticle;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlockEntities;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlocks;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsParticleTypes;
import net.fabricmc.api.ClientModInitializer;
// 26.1 Fabric API changes BlockRenderLayerMap to ChunkSectionLayerMap
//? if <1.21.6 {
/*import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
*///? } else if <26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//? } else if <26.2 {
/*//import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///? }
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
*///? }
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
//~ if >=26.1 'ParticleFactoryRegistry' -> 'ParticleProviderRegistry'
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
//? if <26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
//? }
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class KyanitePortalsClient implements ClientModInitializer {
    public static final PortalEffectManager PORTAL_EFFECT_MANAGER = new PortalEffectManager();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(PORTAL_EFFECT_MANAGER);
        //~ if >=26.1 'ParticleFactoryRegistry' -> 'ParticleProviderRegistry'
        ParticleFactoryRegistry.getInstance().register(KyanitePortalsParticleTypes.PORTAL, CustomPortalParticle.Provider::new);
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            if (client.level != null) {
                KyanitePortals.reloadListeners(client.level.registryAccess());
            }
        });

        //? if <1.21.6 {
        /*BlockRenderLayerMap.INSTANCE.putBlock(KyanitePortalsBlocks.CUSTOM_PORTAL, RenderType.translucent());
        *///? } else if <26.1 {
        BlockRenderLayerMap.putBlock(KyanitePortalsBlocks.CUSTOM_PORTAL, ChunkSectionLayer.TRANSLUCENT);
        //? }

        //? if <26.1 {
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (blockAndTintGetter == null || !(blockAndTintGetter.getBlockEntity(blockPos) instanceof CustomPortalBlockEntity blockEntity)) return 0xFFFFFF;
            return blockEntity.getColor();
        }, KyanitePortalsBlocks.CUSTOM_PORTAL);
        //? } else {
        /*BlockColorRegistry.register((state, blockAndTintGetter, blockPos, tintValues) -> {
            if (!(blockAndTintGetter.getBlockEntity(blockPos) instanceof CustomPortalBlockEntity blockEntity)) return;
            tintValues.add(blockEntity.getColor() | 0xFF000000);
        }, KyanitePortalsBlocks.CUSTOM_PORTAL);
        *///? }
    }
}
