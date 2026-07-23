package dev.kyanitemods.kyaniteportals.content.actions;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
 *///? } else
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SendMessageAction extends PortalAction<SendMessageAction> {
    //$ map_codec_swap SendMessageAction
    public static final com.mojang.serialization.MapCodec<SendMessageAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Settings.optionalLocation(),
            CodecHelper.COMPONENT_CODEC.fieldOf("text").forGetter(SendMessageAction::getText),
            Codec.BOOL.fieldOf("overlay").forGetter(SendMessageAction::isOverlay)
    ).apply(instance, SendMessageAction::new));

    private final Component text;
    private final boolean overlay;

    public SendMessageAction(Settings settings, Component text, boolean overlay) {
        super(settings);
        this.text = text;
        this.overlay = overlay;
    }

    @Override
    public PortalActionType<SendMessageAction> getType() {
        return PortalActions.SEND_MESSAGE;
    }

    public Component getText() {
        return text;
    }

    public boolean isOverlay() {
        return overlay;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data) {
        if (!(entity instanceof Player player)) return PortalActionResult.FAILURE;
        player.displayClientMessage(getText(), isOverlay());
        return PortalActionResult.SUCCESS;
    }
}
