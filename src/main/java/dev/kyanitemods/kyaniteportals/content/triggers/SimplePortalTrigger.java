package dev.kyanitemods.kyaniteportals.content.triggers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    protected TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, Predicate<I> predicate, Consumer<I> beforeAction, BiConsumer<I, TriggerResult> onAction) {
        TriggerResult result = TriggerResult.FAIL;
        if (!listeners.isEmpty()) {
            for (Listener<I> listener : listeners) {
                I instance = listener.getTriggerInstance();
                if (predicate.test(instance)) {
                    beforeAction.accept(instance);
                    TriggerResult partial = listener.run(level, pos, player);
                    onAction.accept(instance, partial);

                    if (partial == TriggerResult.PASS) result = partial;
                }
            }
        }
        return result;
    }
}
