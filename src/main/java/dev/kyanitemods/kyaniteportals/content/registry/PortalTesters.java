package dev.kyanitemods.kyaniteportals.content.registry;

import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.testers.CodecPortalTesterType;
import dev.kyanitemods.kyaniteportals.content.testers.PortalTester;
import dev.kyanitemods.kyaniteportals.content.testers.PortalTesterType;
import dev.kyanitemods.kyaniteportals.content.testers.RectanglePortalTester;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public final class PortalTesters {
    private PortalTesters() {}

    public static final ResourceKey<Registry<PortalTesterType<?>>> RESOURCE_KEY = ResourceKey.createRegistryKey(KyanitePortals.id("portal_tester"));
    public static final Registry<PortalTesterType<?>> REGISTRY = FabricRegistryBuilder.createSimple(RESOURCE_KEY).buildAndRegister();
    public static final Codec<PortalTester<?>> CODEC = REGISTRY.byNameCodec().dispatch("type", PortalTester::getType, PortalTesterType::codec);

    public static final CodecPortalTesterType<RectanglePortalTester> RECTANGLE = register("rectangle", new CodecPortalTesterType<>(RectanglePortalTester.CODEC));

    private static <T extends PortalTester<T>, U extends PortalTesterType<T>> U register(String id, U type) {
        return register(KyanitePortals.id(id), type);
    }

    public static <T extends PortalTester<T>, U extends PortalTesterType<T>> U register(Identifier id, U type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static void load() {
        KyanitePortals.LOGGER.debug("Loading portal tester registry");
    }
}
