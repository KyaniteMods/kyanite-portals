package dev.kyanitemods.kyaniteportals.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public abstract class Range<N extends Number> {
    protected final Optional<N> min;
    protected final Optional<N> max;

    protected Range(Optional<N> min, Optional<N> max) {
        this.min = min;
        this.max = max;
    }

    public boolean isAny() {
        return getMin().isEmpty() && getMax().isEmpty();
    }

    public boolean isExactly() {
        return getMin().isPresent() && getMax().isPresent() && getMin().get().equals(getMax().get());
    }

    public Optional<N> getMin() {
        return min;
    }

    public Optional<N> getMax() {
        return max;
    }

    public abstract boolean matches(N number);

    public static class Int extends Range<Integer> {
        public static final Codec<Int> CODEC_RANGE = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("min").forGetter(Int::getMin),
                Codec.INT.optionalFieldOf("max").forGetter(Int::getMax)
        ).apply(instance, (min, max) -> Int.create(min.orElse(null), max.orElse(null))));

        public static final Codec<Int> CODEC_EXACTLY = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("exactly").forGetter(value -> value.getMin().orElse(0))
        ).apply(instance, value -> Int.create(value, value)));

        public static final Codec<Int> CODEC = Codec.either(CODEC_EXACTLY, CODEC_RANGE).xmap(
                either -> either.map(Function.identity(), Function.identity()),
                value -> value.isExactly() ? Either.right(value) : Either.left(value)
        );

        protected Int(Optional<Integer> min, Optional<Integer> max) {
            super(min, max);
        }

        public static Int create(Integer min, Integer max) {
            return new Int(Optional.ofNullable(min), Optional.ofNullable(max));
        }

        public static Int exactly(Integer value) {
            return create(value, value);
        }

        public static Int min(Integer value) {
            return create(value, null);
        }

        public static Int max(Integer value) {
            return create(null, value);
        }

        @Override
        public boolean matches(@NotNull Integer number) {
            return (getMin().isEmpty() || number >= getMin().get()) && (getMax().isEmpty() || number <= getMax().get());
        }
    }
}
