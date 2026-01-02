package dev.kyanitemods.kyaniteportals.content.portalactions;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class PlayLocalSoundAction extends PortalAction<PlayLocalSoundAction> {
    //$ map_codec_swap PlayLocalSoundAction
    public static final MapCodec<PlayLocalSoundAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation(),
            SoundEvent.CODEC.fieldOf("sound_event").forGetter(PlayLocalSoundAction::soundEvent),
            FloatProvider.CODEC.fieldOf("volume").forGetter(PlayLocalSoundAction::volume),
            FloatProvider.CODEC.fieldOf("pitch").forGetter(PlayLocalSoundAction::pitch)
    ).apply(instance, PlayLocalSoundAction::new));

    private final Holder<SoundEvent> soundEvent;
    private final FloatProvider volume;
    private final FloatProvider pitch;

    public PlayLocalSoundAction(Settings settings, Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) {
        super(settings);
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public PortalActionType<PlayLocalSoundAction> getType() {
        return PortalActions.PLAY_LOCAL_SOUND;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);
        if (level.isClientSide() && entity == null) {
            level.playLocalSound(location.position().x(), location.position().y(), location.position().z(), soundEvent().value(), SoundSource.AMBIENT, volume().sample(level.getRandom()), pitch().sample(level.getRandom()), false);
            return PortalActionResult.SUCCESS;
        }
        if (!(entity instanceof ServerPlayer player)) return PortalActionResult.FAILURE;

        Optional<ServerLevel> optional = location.getWorld(level);
        if (optional.isEmpty() || !KyanitePortalsUtil.getIdentifier(entity.level().dimension()).equals(KyanitePortalsUtil.getIdentifier(optional.get().dimension()))) return PortalActionResult.FAILURE;

        player.connection.send(new ClientboundSoundPacket(soundEvent(), SoundSource.AMBIENT, location.position().x(), location.position().y(), location.position().z(), volume().sample(level.getRandom()), pitch().sample(level.getRandom()), level.getRandom().nextLong()));
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
