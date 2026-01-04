package dev.kyanitemods.kyaniteportals.content.actions.location;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

public record ActionLocation(ResourceKey<LevelStem> dimension, Vec3 position) {
    public Optional<ServerLevel> getWorld(Level level) {
        if (level.isClientSide()) return Optional.empty();
        final ServerLevel serverLevel = Objects.requireNonNull(level.getServer()).getLevel(Registries.levelStemToLevel(dimension()));
        return Optional.ofNullable(serverLevel);
    }
}
