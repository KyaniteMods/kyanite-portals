package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class LevelEventTrigger extends SimplePortalTrigger<LevelEventTrigger.LevelEventTriggerInstance> {
    @Override
    public /*? if <1.20.6 {*//*Codec<LevelEventTriggerInstance>*//*? } else {*/MapCodec<LevelEventTriggerInstance>/*? }*/ codec() {
        return LevelEventTriggerInstance.CODEC;
    }

    public TriggerResult trigger(Level level, int type, BlockPos pos, int data) {
        return trigger(level, pos, null, instance -> LevelEventTriggerInstance.POSITIONS, (instance, triggerPos) -> instance.matches(type, triggerPos, data), (instance, triggerPos) -> instance.beforeTrigger(level, type, triggerPos, data), (instance, triggerPos, result) -> instance.onTrigger(result, level, type, triggerPos, data));
    }

    public LevelEventTriggerInstance create(int type) {
        return new LevelEventTriggerInstance(type, Optional.empty());
    }

    public LevelEventTriggerInstance create(int type, int data) {
        return new LevelEventTriggerInstance(type, Optional.of(data));
    }

    public static class LevelEventTriggerInstance extends AbstractPortalTriggerInstance<LevelEventTriggerInstance> {
        public static final List<Vec3i> POSITIONS = List.of(Vec3i.ZERO);

        //$ map_codec_swap LevelEventTriggerInstance
        public static final MapCodec<LevelEventTriggerInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("type").forGetter(i -> i.type),
                Codec.INT.optionalFieldOf("data").forGetter(i -> i.data)
        ).apply(instance, LevelEventTriggerInstance::new));

        private final int type;
        private final Optional<Integer> data;

        public LevelEventTriggerInstance(int type, Optional<Integer> data) {
            super(PortalTriggers.LEVEL_EVENT);
            this.type = type;
            this.data = data;
        }

        public boolean matches(int type, BlockPos pos, int data) {
            return type == this.type && (this.data.isEmpty() || data == this.data.get());
        }

        public void onTrigger(TriggerResult result, Level level, int type, BlockPos pos, int data) {
        }

        public void beforeTrigger(Level level, int type, BlockPos pos, int data) {
        }
    }
}
