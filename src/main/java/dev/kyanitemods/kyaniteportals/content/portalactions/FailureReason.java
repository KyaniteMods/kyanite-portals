package dev.kyanitemods.kyaniteportals.content.portalactions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Set;

public enum FailureReason implements StringRepresentable {
    PROBABILITY("probability"),
    PREDICATE("predicate"),
    ACTION("action");

    public static final Codec<FailureReason> CODEC = StringRepresentable.fromEnum(FailureReason::values);
    public static final Codec<Set<FailureReason>> SET_CODEC = CODEC.listOf().xmap(Set::copyOf, List::copyOf);
    public static final Codec<Set<FailureReason>> SIMPLE_OR_SET_CODEC = Codec.either(CODEC, SET_CODEC).xmap(
            either -> either.left().isPresent() ? Set.of(either.left().get()) : either.right().get(),
            set -> set.size() == 1 ? Either.left(set.iterator().next()) : Either.right(set)
    );

    private final String id;

    FailureReason(String id) {
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
