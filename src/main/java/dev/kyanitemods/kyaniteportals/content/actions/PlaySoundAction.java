package dev.kyanitemods.kyaniteportals.content.actions;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.actions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlaySoundAction extends PortalAction<PlaySoundAction> {
    //$ map_codec_swap PlaySoundAction
    public static final MapCodec<PlaySoundAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation(),
            SoundEvent.CODEC.fieldOf("sound_event").forGetter(PlaySoundAction::soundEvent),
            FloatProvider.CODEC.fieldOf("volume").forGetter(PlaySoundAction::volume),
            FloatProvider.CODEC.fieldOf("pitch").forGetter(PlaySoundAction::pitch)
    ).apply(instance, PlaySoundAction::new));

    private final Holder<SoundEvent> soundEvent;
    private final FloatProvider volume;
    private final FloatProvider pitch;

    public PlaySoundAction(Settings settings, Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) {
        super(settings);
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public PortalActionType<PlaySoundAction> getType() {
        return PortalActions.PLAY_SOUND;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);
        if (level.isClientSide()) {
            level.playLocalSound(location.position().x(), location.position().y(), location.position().z(), soundEvent().value(), SoundSource.AMBIENT, volume().sample(level.getRandom()), pitch().sample(level.getRandom()), false);
            return PortalActionResult.SUCCESS;
        }

        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty()) return PortalActionResult.FAILURE;

        level.playSeededSound(null, location.position().x(), location.position().y(), location.position().z(), soundEvent(), SoundSource.AMBIENT, volume().sample(level.getRandom()), pitch().sample(level.getRandom()), level.getRandom().nextLong());
        return PortalActionResult.SUCCESS;
    }

    public Holder<SoundEvent> soundEvent() {
        return soundEvent;
    }

    public FloatProvider volume() {
        return volume;
    }

    public FloatProvider pitch() {
        return pitch;
    }
}
