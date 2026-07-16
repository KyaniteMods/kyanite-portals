package dev.kyanitemods.kyaniteportals.util.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

// This may seem redundant but Mojang for some reason only allows WorldGenLevel to use vanilla block predicates
public interface BlockPredicateType<T extends BlockPredicate> {
    ResourceKey<Registry<BlockPredicateType<?>>> RESOURCE_KEY = ResourceKey.createRegistryKey(KyanitePortals.id("block_predicate_type"));
    Registry<BlockPredicateType<?>> REGISTRY = FabricRegistryBuilder.createSimple(RESOURCE_KEY).buildAndRegister();

    BlockPredicateType<SimpleBlockPredicate> SIMPLE = register("simple", () -> SimpleBlockPredicate.CODEC);
    BlockPredicateType<AllOfBlockPredicate> ALL_OF = register("all_of", () -> AllOfBlockPredicate.CODEC);
    BlockPredicateType<AnyOfBlockPredicate> ANY_OF = register("any_of", () -> AnyOfBlockPredicate.CODEC);
    BlockPredicateType<NotBlockPredicate> NOT = register("not", () -> NotBlockPredicate.CODEC);
    BlockPredicateType<HasSturdyFaceBlockPredicate> HAS_STURDY_FACE = register("has_sturdy_face", () -> HasSturdyFaceBlockPredicate.CODEC);
    BlockPredicateType<TrueBlockPredicate> TRUE = register("true", () -> TrueBlockPredicate.CODEC);
    BlockPredicateType<VanillaBlockPredicate> VANILLA = register("vanilla", () -> VanillaBlockPredicate.CODEC);

    /*? if <1.20.5 {*//*Codec<T>*//*? } else { */MapCodec<T>/*? }*/ codec();

    private static <T extends BlockPredicate, U extends BlockPredicateType<T>> U register(String id, U type) {
        return register(KyanitePortals.id(id), type);
    }

    static <T extends BlockPredicate, U extends BlockPredicateType<T>> U register(Identifier id, U type) {
        return Registry.register(REGISTRY, id, type);
    }

    static void load() {
        KyanitePortals.LOGGER.debug("Loading block predicate registry");
    }
}
