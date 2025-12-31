package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//? if >=1.21.3
//import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalEffectManager extends /*? if <1.21.3 {*/SimpleJsonResourceReloadListener/*? } else*//*SimpleJsonResourceReloadListener<Set<PortalEffectOptions<?>>>*/ implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final ResourceLocation ID = KyanitePortals.id("portal_effect");
    public static final Codec<Set<PortalEffectOptions<?>>> CODEC = PortalEffects.CODEC.listOf().xmap(Set::copyOf, List::copyOf);

    private ImmutableMap<ResourceLocation, Set<PortalEffectOptions<?>>> map = ImmutableMap.of();

    //? if <1.21.3 {
    public PortalEffectManager() {
        super(GSON, ID.getPath());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, Set<PortalEffectOptions<?>>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            try {
                Set<PortalEffectOptions<?>> list = CODEC.fieldOf("values").codec().parse(JsonOps.INSTANCE, entry.getValue()).result().orElseThrow(() -> new JsonParseException("Parsing error loading portal effects for portal " + identifier));
                KyanitePortals.LOGGER.info("Loaded portal effects for portal " + identifier);
                builder.put(identifier, list);
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                KyanitePortals.LOGGER.error("Parsing error loading portal effects for portal {}", identifier, runtimeException);
            }
        }
        for (Map.Entry<ResourceKey<Portal>, Integer> entry : CustomPortalBlockEntity.COLORS.entrySet()) {
            if (object.containsKey(entry.getKey().location())) continue;
            builder.put(entry.getKey().location(), Set.of(PortalEffects.NAUSEA, PortalEffects.CLOSE_SCREENS, new TextureOverlayPortalEffectOptions(InventoryMenu.BLOCK_ATLAS, KyanitePortals.id("block/custom_portal"), entry.getValue())));
        }
        map = builder.build();
        KyanitePortals.LOGGER.info("Loaded portal effects for {} {}", map.size(), map.size() == 1 ? "portal" : "portals");
    }
    //? } else {
    /*public PortalEffectManager() {
        super(CODEC, FileToIdConverter.json(ID.getPath()));
    }

    @Override
    protected void apply(Map<ResourceLocation, Set<PortalEffectOptions<?>>> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.map = ImmutableMap.copyOf(map);
    }*/
    //? }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    public Set<PortalEffectOptions<?>> get(ResourceLocation portal) {
        return map.getOrDefault(portal, Set.of());
    }
}
