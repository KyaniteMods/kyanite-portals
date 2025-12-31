package dev.kyanitemods.kyaniteportals.content.registry;

import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.generators.CodecPortalGeneratorType;
import dev.kyanitemods.kyaniteportals.content.generators.NetherLikePortalGenerator;
import dev.kyanitemods.kyaniteportals.content.generators.PortalGenerator;
import dev.kyanitemods.kyaniteportals.content.generators.PortalGeneratorType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class PortalGenerators {
    private PortalGenerators() {}

    public static final ResourceKey<Registry<PortalGeneratorType<?>>> RESOURCE_KEY = ResourceKey.createRegistryKey(KyanitePortals.id("portal_generator"));
    public static final Registry<PortalGeneratorType<?>> REGISTRY = FabricRegistryBuilder.createSimple(RESOURCE_KEY).buildAndRegister();
    public static final Codec<PortalGenerator<?>> CODEC = REGISTRY.byNameCodec().dispatch("type", PortalGenerator::getType, PortalGeneratorType::codec);

    public static final CodecPortalGeneratorType<NetherLikePortalGenerator> NETHER_LIKE = register("nether_like", new CodecPortalGeneratorType<>(NetherLikePortalGenerator.CODEC));

    private static <T extends PortalGenerator<T>, U extends PortalGeneratorType<T>> U register(String id, U type) {
        return register(KyanitePortals.id(id), type);
    }

    public static <T extends PortalGenerator<T>, U extends PortalGeneratorType<T>> U register(ResourceLocation id, U type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Loading portal generator registry");
    }
}
