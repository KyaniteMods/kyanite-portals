package dev.kyanitemods.kyaniteportals.content.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.actions.location.ActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.actions.location.FullActionLocationOptions;
import dev.kyanitemods.kyaniteportals.content.registry.PortalActions;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class PortalAction<T extends PortalAction<T>> {
    private final Settings settings;

    public PortalAction(Settings settings) {
        this.settings = settings;
    }

    public abstract PortalActionType<T> getType();

    public Settings getSettings() {
        return settings;
    }

    public List<PortalAction<?>> onFailure(FailureReason reason) {
        if (reason == FailureReason.PREDICATE) System.out.println(getSettings().onFailure().size());
        return getSettings().onFailure().stream().filter(action -> {
            return action.getSettings().failureReasons().isEmpty() || action.getSettings().failureReasons().get().contains(reason);
        }).toList();
    }

    public abstract PortalActionResult execute(Level level, BlockPos pos, @Nullable Entity entity, ActionExecutionData data);

    public record Settings(Optional<EntityPredicate> predicate, float probability, @NotNull List<PortalAction<?>> onSuccess, @NotNull List<PortalAction<?>> onFailure, Optional<Set<FailureReason>> failureReasons, PortalActionEnvironment environment, ActionLocationOptions locationOptions) {
        public static final Settings DEFAULT = new Settings(Optional.empty(), 1.0f, List.of(), List.of(), Optional.empty(), PortalActionEnvironment.SERVER, FullActionLocationOptions.DEFAULT);

        public static <T extends PortalAction<T>> RecordCodecBuilder<T, Settings> optionalLocation() {
            return OPTIONAL_LOCATION_CODEC.optionalFieldOf("settings", DEFAULT).forGetter(PortalAction::getSettings);
        }

        public static final Codec<Settings> REQUIRED_LOCATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecHelper.ENTITY_PREDICATE_CODEC.optionalFieldOf("predicate").forGetter(Settings::predicate),
                Codec.FLOAT.optionalFieldOf("probability", DEFAULT.probability()).forGetter(Settings::probability),
                PortalActions.CODEC.listOf().optionalFieldOf("on_success", DEFAULT.onSuccess()).forGetter(Settings::onSuccess),
                PortalActions.CODEC.listOf().optionalFieldOf("on_failure", DEFAULT.onFailure()).forGetter(Settings::onFailure),
                FailureReason.SIMPLE_OR_SET_CODEC.optionalFieldOf("failure_reason").forGetter(Settings::failureReasons),
                PortalActionEnvironment.CODEC.optionalFieldOf("environment", PortalActionEnvironment.SERVER).forGetter(Settings::environment),
                ActionLocationOptions.CODEC.fieldOf("location_options").forGetter(Settings::locationOptions)
        ).apply(instance, Settings::new));

        public static final Codec<Settings> OPTIONAL_LOCATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecHelper.ENTITY_PREDICATE_CODEC.optionalFieldOf("predicate").forGetter(Settings::predicate),
                Codec.FLOAT.optionalFieldOf("probability", 1.0f).forGetter(Settings::probability),
                PortalActions.CODEC.listOf().optionalFieldOf("on_success", List.of()).forGetter(Settings::onSuccess),
                PortalActions.CODEC.listOf().optionalFieldOf("on_failure", List.of()).forGetter(Settings::onFailure),
                FailureReason.SIMPLE_OR_SET_CODEC.optionalFieldOf("failure_reason").forGetter(Settings::failureReasons),
                PortalActionEnvironment.CODEC.optionalFieldOf("environment", PortalActionEnvironment.SERVER).forGetter(Settings::environment),
                ActionLocationOptions.CODEC.optionalFieldOf("location_options", FullActionLocationOptions.DEFAULT).forGetter(Settings::locationOptions)
        ).apply(instance, Settings::new));

        public static final class Builder {
            private Builder() {}

            private Optional<EntityPredicate> predicate = DEFAULT.predicate();
            private float probability = DEFAULT.probability();
            private List<PortalAction<?>> onSuccess = new ArrayList<>();
            private List<PortalAction<?>> onFailure = new ArrayList<>();
            private Optional<Set<FailureReason>> failureReasons = Optional.empty();
            private PortalActionEnvironment environment = DEFAULT.environment();
            private ActionLocationOptions locationOptions = DEFAULT.locationOptions();

            public static Builder create() {
                return new Builder();
            }

            public Builder predicate(EntityPredicate predicate) {
                this.predicate = Optional.ofNullable(predicate);
                return this;
            }

            public Builder probability(float probability) {
                this.probability = probability;
                return this;
            }

            public Builder onSuccess(PortalAction<?>... actions) {
                Collections.addAll(this.onSuccess, actions);
                return this;
            }

            public Builder onFailure(PortalAction<?>... actions) {
                Collections.addAll(this.onFailure, actions);
                return this;
            }

            public Builder failureReasons(FailureReason... reasons) {
                this.failureReasons = Optional.of(new HashSet<>(Arrays.asList(reasons)));
                return this;
            }

            public Builder environment(PortalActionEnvironment environment) {
                this.environment = environment;
                return this;
            }

            public Builder locationOptions(ActionLocationOptions locationOptions) {
                this.locationOptions = locationOptions;
                return this;
            }

            public Settings build() {
                return new Settings(predicate, probability, onSuccess, onFailure, failureReasons, environment, locationOptions);
            }
        }
    }
}
