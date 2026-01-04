package dev.kyanitemods.kyaniteportals.content.actions.location;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.actions.ActionExecutionData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record LoadActionLocationOptions(String key) implements ActionLocationOptions {
    public static final Codec<LoadActionLocationOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("load").forGetter(LoadActionLocationOptions::key)
    ).apply(instance, LoadActionLocationOptions::new));

    @Override
    public ActionLocation get(Level level, BlockPos pos, Entity entity, RandomSource random, ActionExecutionData data) throws IllegalArgumentException {
        ActionLocation location = data.get(key());
        if (location == null) throw new IllegalArgumentException("Location " + key() + " was not stored");
        return location;
    }
}
