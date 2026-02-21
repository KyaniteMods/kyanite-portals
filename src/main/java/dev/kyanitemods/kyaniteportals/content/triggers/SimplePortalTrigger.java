package dev.kyanitemods.kyaniteportals.content.triggers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;

public abstract class SimplePortalTrigger<I extends PortalTriggerInstance<I>> implements PortalTrigger<I> {
    private final Set<Listener<I>> listeners = new HashSet<>();

    @Override
    public final void addListener(Listener<I> listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(Listener<I> listener) {
        listeners.remove(listener);
    }

    @Override
    public final void removeListeners() {
        listeners.clear();
    }

    protected TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, Function<I, List<Vec3i>> positions, BiPredicate<I, BlockPos> predicate, BiConsumer<I, BlockPos> beforeAction, TriConsumer<I, BlockPos, TriggerResult> onAction) {
        TriggerResult result = TriggerResult.FAIL;
        if (!listeners.isEmpty()) {
            BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
            for (Listener<I> listener : listeners) {
                I instance = listener.getTriggerInstance();
                for (Vec3i vec3i : positions.apply(instance)) {
                    mutableBlockPos.setWithOffset(pos, vec3i);
                    if (predicate.test(instance, mutableBlockPos)) {
                        BlockPos immutable = mutableBlockPos.immutable();
                        beforeAction.accept(instance, immutable);
                        TriggerResult partial = listener.run(level, immutable, player);
                        onAction.accept(instance, immutable, partial);

                        if (partial == TriggerResult.PASS) result = partial;
                    }
                }
            }
        }
        return result;
    }
}
