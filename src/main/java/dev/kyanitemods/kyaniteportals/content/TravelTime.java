package dev.kyanitemods.kyaniteportals.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.kyanitemods.kyaniteportals.content.interfaces.GameModeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.util.Map;

public record TravelTime(Map<GameType, Integer> gameModes, int nonPlayer, int defaultTime) {
    public static final Codec<TravelTime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            new UnboundedMapCodec<>(GameType.CODEC, Codec.INT).fieldOf("game_modes").forGetter(TravelTime::gameModes),
            Codec.INT.fieldOf("non_player").forGetter(TravelTime::nonPlayer),
            Codec.INT.fieldOf("default_time").forGetter(TravelTime::defaultTime)
    ).apply(instance, TravelTime::new));
    public static final TravelTime DEFAULT = new TravelTime(Map.of(
            GameType.SURVIVAL, 80,
            GameType.CREATIVE, 0,
            GameType.ADVENTURE, 80,
            GameType.SPECTATOR, 0
    ), 0, 80);

    public int get(Entity entity) {
        if (!(entity instanceof Player player)) return nonPlayer();
        GameType type = ((GameModeEntity) player).kyanitePortals$getGameMode();
        return gameModes().getOrDefault(type, defaultTime());
    }
}
