package dev.kyanitemods.kyaniteportals.content.triggers;

//? if <1.20.6 {
/*import com.mojang.serialization.Codec;
*///? } else
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface PortalTrigger<I extends PortalTriggerInstance<I>> {
    void addListener(Listener<I> listener);

    void removeListener(Listener<I> listener);

    void removeListeners();

    /*? if <1.20.6 {*//*Codec<I>*//*? } else {*/MapCodec<I>/*? }*/ codec();

    class Listener<I extends PortalTriggerInstance<I>> {
        private final I instance;
        private final TriggerAction action;

        public Listener(I instance, TriggerAction action) {
            this.instance = instance;
            this.action = action;
        }

        public I getTriggerInstance() {
            return instance;
        }

        public TriggerResult run(Level level, BlockPos pos, @Nullable Player player) {
            return action.run(instance, level, pos, player);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            Listener<?> listener = (Listener<?>) object;

            if (!instance.equals(listener.instance)) return false;
            return action.equals(listener.action);
        }

        @Override
        public int hashCode() {
            int result = instance.hashCode();
            result = 31 * result + action.hashCode();
            return result;
        }
    }
}
