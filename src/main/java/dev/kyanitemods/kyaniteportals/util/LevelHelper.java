package dev.kyanitemods.kyaniteportals.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class LevelHelper {
    private LevelHelper() {}

    public static Holder<LevelStem> asStemHolder(Level level) {
        HolderLookup.RegistryLookup<LevelStem> registry = level.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        ResourceKey<LevelStem> stemKey = Registries.levelToLevelStem(level.dimension());
        return registry.getOrThrow(stemKey);
    }

    public static Holder<LevelStem> asStemReference(Level level) {
        HolderLookup.RegistryLookup<LevelStem> registry = level.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
        ResourceKey<LevelStem> stemKey = Registries.levelToLevelStem(level.dimension());
        return Holder.Reference.createStandAlone(registry, stemKey); // NOTE: may crash on the client
    }
}
