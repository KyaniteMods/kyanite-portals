package dev.kyanitemods.kyaniteportals.content.portalactions.location;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.kyanitemods.kyaniteportals.content.portalactions.ActionExecutionData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface ActionLocationOptions {
    Codec<ActionLocationOptions> CODEC = Codec.either(FullActionLocationOptions.CODEC, LoadActionLocationOptions.CODEC).xmap(either -> {
        if (either.left().isPresent()) return either.left().get();
        return either.right().get();
    }, options -> (options instanceof FullActionLocationOptions fullAction) ? Either.left(fullAction) : Either.right((LoadActionLocationOptions) options));

    ActionLocation get(Level level, BlockPos pos, Entity entity, RandomSource random, ActionExecutionData data) throws IllegalArgumentException;
}
