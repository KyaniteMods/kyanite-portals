package dev.kyanitemods.kyaniteportals.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
public final class CodecHelper {
    private CodecHelper() {}

    //? if <1.20.2 {
    /*public static final Codec<EntityPredicate> ENTITY_PREDICATE_CODEC = CodecHelper.codec(EntityPredicate::fromJson, EntityPredicate::serializeToJson);
    public static final Codec<StatePropertiesPredicate> STATE_PROPERTIES_PREDICATE_CODEC = CodecHelper.codec(StatePropertiesPredicate::fromJson, StatePropertiesPredicate::serializeToJson);
    public static final Codec<NbtPredicate> NBT_PREDICATE_CODEC = CodecHelper.codec(NbtPredicate::fromJson, NbtPredicate::serializeToJson);
    public static final Codec<ItemPredicate> ITEM_PREDICATE_CODEC = CodecHelper.codec(ItemPredicate::fromJson, ItemPredicate::serializeToJson);
    *///? } else {
    public static final Codec<EntityPredicate> ENTITY_PREDICATE_CODEC = EntityPredicate.CODEC;
    public static final Codec<StatePropertiesPredicate> STATE_PROPERTIES_PREDICATE_CODEC = StatePropertiesPredicate.CODEC;
    public static final Codec<NbtPredicate> NBT_PREDICATE_CODEC = NbtPredicate.CODEC;
    public static final Codec<ItemPredicate> ITEM_PREDICATE_CODEC = ItemPredicate.CODEC;
    //? }

    //? if <1.21.5 {
    /*public static final Codec<CompoundTag> FLATTENED_TAG_CODEC = CodecHelper.codec(json -> {
        try {
            return TagParser.parseTag(GsonHelper.convertToString(json, "nbt"));
        } catch (CommandSyntaxException var3) {
            throw new JsonSyntaxException("Invalid NBT tag: " + var3.getMessage());
        }
    }, nbt -> new JsonPrimitive(nbt.toString()));
    *///? } else
    public static final Codec<CompoundTag> FLATTENED_TAG_CODEC = TagParser.FLATTENED_CODEC;

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(Products.P5<F, T1, T2, T3, T4, T5> p5, App<F, T6> t6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9) {
        return new Products.P9<>(p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5(), t6, t7, t8, t9);
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> and(Products.P6<F, T1, T2, T3, T4, T5, T6> p6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9) {
        return new Products.P9<>(p6.t1(), p6.t2(), p6.t3(), p6.t4(), p6.t5(), p6.t6(), t7, t8, t9);
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> and(Products.P6<F, T1, T2, T3, T4, T5, T6> p6, App<F, T7> t7, App<F, T8> t8, App<F, T9> t9, App<F, T10> t10) {
        return new Products.P10<>(p6.t1(), p6.t2(), p6.t3(), p6.t4(), p6.t5(), p6.t6(), t7, t8, t9, t10);
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p7, App<F, T8> t8, App<F, T9> t9, App<F, T10> t10) {
        return new Products.P10<>(p7.t1(), p7.t2(), p7.t3(), p7.t4(), p7.t5(), p7.t6(), p7.t7(), t8, t9, t10);
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p7, App<F, T8> t8, App<F, T9> t9, App<F, T10> t10, App<F, T11> t11) {
        return new Products.P11<>(p7.t1(), p7.t2(), p7.t3(), p7.t4(), p7.t5(), p7.t6(), p7.t7(), t8, t9, t10, t11);
    }

    public static <T> Codec<T> codec(Function<JsonElement, T> decoder, Function<T, JsonElement> encoder) {
        return ExtraCodecs.JSON.flatXmap(jsonElement -> {
            try {
                return DataResult.success(decoder.apply(jsonElement));
            } catch (JsonParseException var2) {
                return DataResult.error(var2::getMessage);
            }
        }, t -> {
            try {
                return DataResult.success(encoder.apply(t));
            } catch (IllegalArgumentException var2) {
                return DataResult.error(var2::getMessage);
            }
        });
    }

    public static <T> Codec<T> simpleEither(Codec<T> first, Codec<T> second) {
        return Codec.either(first, second).xmap(
                either -> either.map(Function.identity(), Function.identity()),
                Either::left
        );
    }

    public static <T> Codec<T> unitCodec(T instance) {
        //? if <1.21.11 {
        /*return Codec.unit(instance);
        *///? } else
        return MapCodec.unitCodec(instance);
    }

    public static <T> MapCodec<T> unitMapCodec(T instance) {
        return MapCodec.unit(instance);
    }
}
