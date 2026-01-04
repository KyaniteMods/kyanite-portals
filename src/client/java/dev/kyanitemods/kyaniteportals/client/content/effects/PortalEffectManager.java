package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//? if <1.21.5
//import net.minecraft.client.renderer.texture.TextureAtlas;
//? if >=1.21.3
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
//? if <1.21.3 {
/*import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
*///? }

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PortalEffectManager extends /*? if <1.21.3 {*//*SimpleJsonResourceReloadListener*//*? } else {*/SimpleJsonResourceReloadListener<Set<PortalEffectOptions<?>>>/*? }*/ implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Identifier ID = KyanitePortals.id("portal_effect");
    public static final Codec<Set<PortalEffectOptions<?>>> CODEC = PortalEffects.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("values").codec();

    private ImmutableMap<Identifier, Set<PortalEffectOptions<?>>> map = ImmutableMap.of();

    //? if <1.21.3 {
    /*public PortalEffectManager() {
        super(GSON, ID.getPath());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, Set<PortalEffectOptions<?>>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            try {
                Set<PortalEffectOptions<?>> list = CODEC.parse(JsonOps.INSTANCE, entry.getValue()).result().orElseThrow(() -> new JsonParseException("Parsing error loading portal effects for portal " + identifier));
                KyanitePortals.LOGGER.info("Loaded portal effects for portal " + identifier);
                builder.put(identifier, list);
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                KyanitePortals.LOGGER.error("Parsing error loading portal effects for portal {}", identifier, runtimeException);
            }
        }
        for (Map.Entry<ResourceKey<Portal>, Integer> entry : CustomPortalBlockEntity.COLORS.entrySet()) {
            if (object.containsKey(entry.getKey().location())) continue;
            builder.put(entry.getKey().location(), Set.of(
                    PortalEffects.NAUSEA,
                    PortalEffects.CLOSE_SCREENS,
                    new TextureOverlayPortalEffectOptions(TextureAtlas.LOCATION_BLOCKS, KyanitePortals.id("block/custom_portal"), entry.getValue()),
                    new NetherLikeLoadingBackgroundOptions(TextureAtlas.LOCATION_BLOCKS, KyanitePortals.id("block/custom_portal"), entry.getValue())
            ));
        }
        map = builder.build();
        KyanitePortals.LOGGER.info("Loaded portal effects for {} {}", map.size(), map.size() == 1 ? "portal" : "portals");
    }
    *///? } else {
    public PortalEffectManager() {
        super(CODEC, FileToIdConverter.json(ID.getPath()));
    }

    @Override
    protected void apply(Map<Identifier, Set<PortalEffectOptions<?>>> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<Identifier, Set<PortalEffectOptions<?>>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceKey<Portal>, Integer> entry : CustomPortalBlockEntity.COLORS.entrySet()) {
            if (map.containsKey(KyanitePortalsUtil.getIdentifier(entry.getKey()))) continue;
            builder.put(KyanitePortalsUtil.getIdentifier(entry.getKey()), Set.of(
                    PortalEffects.NAUSEA,
                    PortalEffects.CLOSE_SCREENS,
                    new TextureOverlayPortalEffectOptions(/*? if >=1.21.5 {*/AtlasIds.BLOCKS/*? } else {*//*TextureAtlas.LOCATION_BLOCKS*//*? }*/, KyanitePortals.id("block/custom_portal"), entry.getValue())/*? if >=1.20.5 {*/,
                    new NetherLikeLoadingBackgroundOptions(/*? if >=1.21.5 {*/AtlasIds.BLOCKS/*? } else {*//*TextureAtlas.LOCATION_BLOCKS*//*? }*/, KyanitePortals.id("block/custom_portal"), entry.getValue())/*? }*/
            ));
        }
        builder.putAll(map);
        this.map = builder.build();
    }
    //? }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    public Set<PortalEffectOptions<?>> get(Identifier portal) {
        return map.getOrDefault(portal, Set.of());
    }
}
