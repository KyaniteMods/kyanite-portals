package dev.kyanitemods.kyaniteportals.content.portalactions;

import com.mojang.serialization.Codec;
//? if >=1.20.6
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocation;
import dev.kyanitemods.kyaniteportals.content.portalactions.location.ActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StoreActionLocationAction extends PortalAction<StoreActionLocationAction> {
    //$ map_codec_swap StoreActionLocationAction
    public static final Codec<StoreActionLocationAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Settings.optionalLocation(),
            Codec.STRING.fieldOf("key").forGetter(StoreActionLocationAction::getKey)
    ).apply(instance, StoreActionLocationAction::new));

    private final String key;

    public StoreActionLocationAction(Settings settings, String key) {
        super(settings);
        this.key = key;
    }

    @Override
    public PortalActionType<StoreActionLocationAction> getType() {
        return PortalActions.STORE_ACTION_LOCATION;
    }

    @Override
    public PortalActionResult execute(Level level, BlockPos pos, Entity entity, ActionExecutionData data) {
        ActionLocation location = getSettings().locationOptions().get(level, pos, entity, level.getRandom(), data);
        data.put(getKey(), location);
        return PortalActionResult.SUCCESS;
    }

    public String getKey() {
        return key;
    }
}
