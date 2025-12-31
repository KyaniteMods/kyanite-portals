package dev.kyanitemods.kyaniteportals.content.registry;

import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.portalactions.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class PortalActions {
    private PortalActions() {}

    public static final ResourceKey<Registry<PortalActionType<?>>> RESOURCE_KEY = ResourceKey.createRegistryKey(KyanitePortals.id("portal_action"));
    public static final Registry<PortalActionType<?>> REGISTRY = FabricRegistryBuilder.createSimple(RESOURCE_KEY).buildAndRegister();
    public static final Codec<PortalAction<?>> CODEC = REGISTRY.byNameCodec().dispatch("action", PortalAction::getType, PortalActionType::codec);

    public static final CodecPortalActionType<PlayLocalSoundAction> PLAY_LOCAL_SOUND = register("play_local_sound", new CodecPortalActionType<>(PlayLocalSoundAction.CODEC));
    public static final CodecPortalActionType<SetPositionAction> SET_POSITION = register("set_position", new CodecPortalActionType<>(SetPositionAction.CODEC));
    public static final CodecPortalActionType<CreateNetherLikePortalAction> CREATE_NETHER_LIKE_PORTAL = register("create_nether_like_portal", new CodecPortalActionType<>(CreateNetherLikePortalAction.CODEC));
    public static final CodecPortalActionType<TeleportToNetherLikePortalPoiAction> TELEPORT_TO_NETHER_LIKE_PORTAL_POI = register("teleport_to_nether_like_portal_poi", new CodecPortalActionType<>(TeleportToNetherLikePortalPoiAction.CODEC));
    public static final CodecPortalActionType<StoreActionLocationAction> STORE_ACTION_LOCATION = register("store_action_location", new CodecPortalActionType<>(StoreActionLocationAction.CODEC));
    public static final CodecPortalActionType<PlaySoundAction> PLAY_SOUND = register("play_sound", new CodecPortalActionType<>(PlaySoundAction.CODEC));
    public static final CodecPortalActionType<SpawnNetherLikePortalParticlesAction> SPAWN_NETHER_LIKE_PORTAL_PARTICLES = register("spawn_nether_like_portal_particles", new CodecPortalActionType<>(SpawnNetherLikePortalParticlesAction.CODEC));
    public static final CodecPortalActionType<TeleportToServerSpawnPointAction> TELEPORT_TO_SERVER_SPAWN_POINT = register("teleport_to_server_spawn_point", new CodecPortalActionType<>(TeleportToServerSpawnPointAction.CODEC));
    public static final CodecPortalActionType<WinGamePortalAction> WIN_GAME = register("win_game", new CodecPortalActionType<>(WinGamePortalAction.CODEC));

    private static <T extends PortalAction<T>, U extends PortalActionType<T>> U register(String id, U type) {
        return register(KyanitePortals.id(id), type);
    }

    public static <T extends PortalAction<T>, U extends PortalActionType<T>> U register(ResourceLocation id, U type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Loading portal action registry");
    }
}
