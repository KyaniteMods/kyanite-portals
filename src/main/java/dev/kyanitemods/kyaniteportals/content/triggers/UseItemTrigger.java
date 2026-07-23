package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.BlockPos;
//? if >=1.21.3
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class UseItemTrigger extends SimplePortalTrigger<UseItemTrigger.UseItemTriggerInstance> {
    static final List<ItemPredicate> PREDICATES = new ArrayList<>();

    @Override
    public /*? if <1.20.6 {*//*Codec<UseItemTriggerInstance>*//*? } else {*/MapCodec<UseItemTriggerInstance>/*? }*/ codec() {
        return UseItemTriggerInstance.CODEC;
    }

    public TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        return trigger(level, pos, player, instance -> UseItemTriggerInstance.POSITIONS, (instance, triggerPos) -> instance.matches(stack), (instance, triggerPos) -> instance.beforeTrigger(level, player, stack), (instance, triggerPos, result) -> instance.onTrigger(result, level, player, stack));
    }

    public UseItemTriggerInstance create(ItemPredicate predicate, int damage) {
        return new UseItemTriggerInstance(predicate, damage);
    }

    public UseItemTriggerInstance create(ItemPredicate predicate) {
        return create(predicate, 0);
    }

    public UseItemTriggerInstance create(/*? if >=1.21.3 {*/HolderGetter<Item> itemLookup, /*? }*/Item item) {
        return create(/*? if >=1.21.3 {*/itemLookup, /*? }*/item, 0);

    }

    public UseItemTriggerInstance create(/*? if >=1.21.3 {*/HolderGetter<Item> itemLookup, /*? }*/Item item, int damage) {
        return create(ItemPredicate.Builder.item().of(/*? if >=1.21.3 {*/itemLookup, /*? }*/item).build(), damage);
    }

    public static boolean shouldSwing(ItemStack stack) {
        return PREDICATES.stream().anyMatch(predicate -> /*? if <1.20.6 {*//*predicate.matches(stack)*//*? } else {*/predicate.test(stack)/*? }*/);
    }

    @Override
    public void onListenersRemoved() {
        super.onListenersRemoved();
        PREDICATES.clear();
    }

    public static class UseItemTriggerInstance extends AbstractPortalTriggerInstance<UseItemTriggerInstance> {
        public static final List<Vec3i> POSITIONS = List.of(Vec3i.ZERO);

        //$ map_codec_swap UseItemTriggerInstance
        public static final com.mojang.serialization.MapCodec<UseItemTriggerInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CodecHelper.ITEM_PREDICATE_CODEC.fieldOf("predicate").forGetter(i -> i.itemPredicate),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_item_by", 0).forGetter(i -> i.damageItemBy)
        ).apply(instance, UseItemTriggerInstance::new));

        private final ItemPredicate itemPredicate;
        private final int damageItemBy;

        public UseItemTriggerInstance(ItemPredicate itemPredicate, int damageItemBy) {
            super(PortalTriggers.USE_ITEM);
            this.itemPredicate = itemPredicate;
            this.damageItemBy = damageItemBy;
        }

        public boolean matches(ItemStack stack) {
            //? if <1.20.6 {
            /*return itemPredicate.matches(stack);
            *///? } else
            return itemPredicate.test(stack);
        }

        public void onTrigger(TriggerResult result, Level level, @Nullable Player player, ItemStack stack) {
            if (result == TriggerResult.PASS && damageItemBy > 0 && !level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
                if (stack.isStackable()) stack.shrink(damageItemBy);
                else if (stack.isDamageableItem()) {
                    //? if <1.20.6 {
                    /*stack.hurt(damageItemBy, level.getRandom(), ((ServerPlayer) player));
                    *///? } else if <1.21 {
                    //stack.hurtAndBreak(damageItemBy, level.getRandom(), ((ServerPlayer) player), () -> {});
                    //? } else
                    stack.hurtAndBreak(damageItemBy, ((net.minecraft.server.level.ServerLevel) level), ((ServerPlayer) player), item -> {});
                }
            }
        }

        @Override
        public void addListener(TriggerAction action) {
            super.addListener(action);
            PREDICATES.add(itemPredicate);
        }

        public void beforeTrigger(Level level, @Nullable Player player, ItemStack stack) {
        }
    }
}
