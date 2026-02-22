package dev.kyanitemods.kyaniteportals.content.registry;

import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.triggers.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

public final class PortalTriggers {
    private PortalTriggers() {}

    public static final ResourceKey<Registry<PortalTrigger<?>>> RESOURCE_KEY = ResourceKey.createRegistryKey(KyanitePortals.id("portal_trigger"));
    public static final Registry<PortalTrigger<?>> REGISTRY = FabricRegistryBuilder.createSimple(RESOURCE_KEY).buildAndRegister();
    public static final Codec<PortalTriggerInstance<?>> CODEC = REGISTRY.byNameCodec().dispatch("type", PortalTriggerInstance::getTrigger, PortalTrigger::codec);

    public static final UseItemTrigger USE_ITEM = register("use_item", new UseItemTrigger());
    @ApiStatus.Experimental
    public static final BlockChangeTrigger BLOCK_CHANGE = register("block_change", new BlockChangeTrigger());
    public static final ThrownPotionTrigger THROWN_POTION = register("thrown_potion", new ThrownPotionTrigger());
    public static final LevelEventTrigger LEVEL_EVENT = register("level_event", new LevelEventTrigger());

    private static <T extends PortalTriggerInstance<T>, U extends PortalTrigger<T>> U register(String id, U type) {
        return register(KyanitePortals.id(id), type);
    }

    public static <T extends PortalTriggerInstance<T>, U extends PortalTrigger<T>> U register(Identifier id, U type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Loading portal trigger registry");
    }
}
