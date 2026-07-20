package dev.kyanitemods.kyaniteportals.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.generators.PortalGenerator;
import dev.kyanitemods.kyaniteportals.content.actions.ActionExecutionData;
import dev.kyanitemods.kyaniteportals.content.actions.FailureReason;
import dev.kyanitemods.kyaniteportals.content.actions.PortalAction;
import dev.kyanitemods.kyaniteportals.content.actions.PortalActionResult;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.content.registry.PortalGenerators;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTesters;
import dev.kyanitemods.kyaniteportals.content.testers.PortalTester;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Portal(Optional<PortalGenerator<?>> generator, Optional<PortalTester<?>> tester, boolean testValidityAfterGeneration, Optional<EntityPredicate> entityPredicate, TravelTime travelTime, List<PortalAction<?>> enterActions, List<PortalAction<?>> travelActions, List<PortalAction<?>> tickActions, List<PortalAction<?>> randomTickActions, List<PortalAction<?>> animationTickActions, boolean spectatorsCanUse) {
    public static final boolean SPECTATORS_CAN_USE_DEFAULT = /*? if <26.3 {*/false/*? } else {*//*true*//*? }*/;

    public static final Codec<Portal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortalGenerators.CODEC.optionalFieldOf("generator").forGetter(Portal::generator),
            PortalTesters.CODEC.optionalFieldOf("tester").forGetter(Portal::tester),
            Codec.BOOL.optionalFieldOf("test_validity_after_generation", true).forGetter(Portal::testValidityAfterGeneration),
            CodecHelper.ENTITY_PREDICATE_CODEC.optionalFieldOf("entity_predicate").forGetter(Portal::entityPredicate),
            TravelTime.CODEC.optionalFieldOf("travel_time", TravelTime.DEFAULT).forGetter(Portal::travelTime),
            PortalActions.CODEC.listOf().optionalFieldOf("enter_actions", List.of()).forGetter(Portal::enterActions),
            PortalActions.CODEC.listOf().optionalFieldOf("travel_actions", List.of()).forGetter(Portal::travelActions),
            PortalActions.CODEC.listOf().optionalFieldOf("tick_actions", List.of()).forGetter(Portal::tickActions),
            PortalActions.CODEC.listOf().optionalFieldOf("random_tick_actions", List.of()).forGetter(Portal::randomTickActions),
            PortalActions.CODEC.listOf().optionalFieldOf("animation_tick_actions", List.of()).forGetter(Portal::animationTickActions),
            Codec.BOOL.optionalFieldOf("spectators_can_use", SPECTATORS_CAN_USE_DEFAULT).forGetter(Portal::spectatorsCanUse)
    ).apply(instance, Portal::new));

    public static void executeAll(Level level, BlockPos pos, @Nullable Entity entity, List<PortalAction<?>> actions) {
        Deque<PortalAction<?>> deque = new ArrayDeque<>(actions);
        ActionExecutionData data = new ActionExecutionData();
        while (!deque.isEmpty()) {
            PortalAction<?> action = deque.removeFirst();
            List<PortalAction<?>> toAdd;
            if (!(level.getRandom().nextFloat() < action.getSettings().probability())) {
                toAdd = action.onFailure(FailureReason.PROBABILITY);
            } else if (action.getSettings().predicate().isPresent() && (level.isClientSide() || entity == null || !action.getSettings().predicate().get().matches(new LootContext.Builder(new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, entity.position()).create(LootContextParamSets.ADVANCEMENT_REWARD)).create(/*? if <1.20.2 { *//*null*//*? } else { */Optional.empty()/*? } */)))) { // NOTE: desync between client and server.
                toAdd = action.onFailure(FailureReason.PREDICATE);
            } else if ((level.isClientSide() && action.getSettings().environment().isClient()) || (!level.isClientSide() && action.getSettings().environment().isServer())) {
                PortalActionResult result = action.execute(level, pos, entity, data);
                toAdd = result == PortalActionResult.SUCCESS ? action.getSettings().onSuccess() : action.onFailure(FailureReason.ACTION);
            } else continue;

            for (int i = toAdd.size() - 1; i >= 0; i--) {
                deque.offerFirst(toAdd.get(i));
            }
        }
    }

    public static class Builder {
        private Optional<PortalGenerator<?>> generator = Optional.empty();
        private Optional<PortalTester<?>> tester = Optional.empty();
        private boolean testValidityAfterGeneration = true;
        private Optional<EntityPredicate> entityPredicate = Optional.empty();
        private TravelTime travelTime = TravelTime.DEFAULT;
        private List<PortalAction<?>> enterActions = new ArrayList<>();
        private List<PortalAction<?>> travelActions = new ArrayList<>();
        private List<PortalAction<?>> tickActions = new ArrayList<>();
        private List<PortalAction<?>> randomTickActions = new ArrayList<>();
        private List<PortalAction<?>> animationTickActions = new ArrayList<>();
        private boolean spectatorsCanUse = SPECTATORS_CAN_USE_DEFAULT;

        protected Builder() {}

        public static Builder create() {
            return new Builder();
        }

        public Builder withGenerator(PortalGenerator<?> generator) {
            this.generator = Optional.ofNullable(generator);
            return this;
        }

        public Builder withTester(PortalTester<?> tester) {
            this.tester = Optional.ofNullable(tester);
            return this;
        }

        public Builder testValidityAfterGeneration(boolean value) {
            testValidityAfterGeneration = value;
            return this;
        }

        public Builder testValidityAfterGeneration() {
            return testValidityAfterGeneration(true);
        }

        public Builder withEntityPredicate(EntityPredicate entityPredicate) {
            this.entityPredicate = Optional.ofNullable(entityPredicate);
            return this;
        }

        public Builder withTravelTime(TravelTime travelTime) {
            this.travelTime = travelTime;
            return this;
        }

        public Builder withEnterActions(PortalAction<?>... enterActions) {
            this.enterActions.addAll(List.of(enterActions));
            return this;
        }

        public Builder withTravelActions(PortalAction<?>... travelActions) {
            this.travelActions.addAll(List.of(travelActions));
            return this;
        }

        public Builder withTickActions(PortalAction<?>... tickActions) {
            this.tickActions.addAll(List.of(tickActions));
            return this;
        }

        public Builder withRandomTickActions(PortalAction<?>... randomTickActions) {
            this.randomTickActions.addAll(List.of(randomTickActions));
            return this;
        }

        public Builder withAnimationTickActions(PortalAction<?>... animationTickActions) {
            this.animationTickActions.addAll(List.of(animationTickActions));
            return this;
        }

        public Builder spectatorsCanUse(boolean value) {
            spectatorsCanUse = value;
            return this;
        }

        public Portal build() {
            return new Portal(generator, tester, testValidityAfterGeneration, entityPredicate, travelTime, enterActions, travelActions, tickActions, randomTickActions, animationTickActions, spectatorsCanUse);
        }
    }
}
