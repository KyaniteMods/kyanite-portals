package dev.kyanitemods.kyaniteportals.client.content.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.client.KyanitePortalsClient;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.Set;

public final class PortalEffects {
    private PortalEffects() {}

    private static final BiMap<Identifier, PortalEffectType<?>> EFFECTS = HashBiMap.create();

    public static final Codec<PortalEffectType<?>> TYPE_CODEC = Identifier.CODEC.flatXmap(id -> {
        PortalEffectType<?> type = EFFECTS.get(id);
        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown portal effect: " + id);
    }, type -> {
        Identifier id = EFFECTS.inverse().get(type);
        return type != null ? DataResult.success(id) : DataResult.error(() -> "Unknown portal effect: " + id);
    });

    public static final Codec<PortalEffectOptions<?>> CODEC = TYPE_CODEC.dispatch("type", PortalEffectOptions::getType, PortalEffectType::codec);

    public static final SimplePortalEffect NAUSEA = register("nausea", new SimplePortalEffect());
    public static final SimplePortalEffect CLOSE_SCREENS = register("close_screens", new SimplePortalEffect());
    public static final CodecPortalEffectType<TextureOverlayPortalEffectOptions> TEXTURE_OVERLAY = register("texture_overlay", new CodecPortalEffectType<>(TextureOverlayPortalEffectOptions.CODEC));
    public static final CodecPortalEffectType<NetherLikeLoadingBackgroundOptions> NETHER_LIKE_LOADING_BACKGROUND = register("loading_background/nether_like", new CodecPortalEffectType<>(NetherLikeLoadingBackgroundOptions.CODEC));

    private static <T extends PortalEffectOptions<T>, P extends PortalEffectType<T>> P register(String id, P type) {
        return register(KyanitePortals.id(id), type);
    }

    public static <T extends PortalEffectOptions<T>, P extends PortalEffectType<T>> P register(Identifier id, P type) {
        EFFECTS.put(id, type);
        return type;
    }

    public static Set<PortalEffectOptions<?>> get(ResourceKey<Portal> portal) {
        return KyanitePortalsClient.PORTAL_EFFECT_MANAGER.get(KyanitePortalsUtil.getIdentifier(portal));
    }

    @SuppressWarnings("unchecked")
    public static <T extends PortalEffectOptions<T>> Optional<T> get(ResourceKey<Portal> portal, PortalEffectType<T> type) {
        for (PortalEffectOptions<?> options : get(portal)) {
            if (options.getType() == type) return Optional.of((T) options);
        }
        return Optional.empty();
    }
}
