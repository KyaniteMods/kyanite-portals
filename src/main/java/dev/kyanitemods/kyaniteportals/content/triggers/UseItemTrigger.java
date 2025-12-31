package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
//import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
//? if >=1.21.3
//import net.minecraft.core.HolderGetter;
//? if >=1.21
//import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class UseItemTrigger extends SimplePortalTrigger<UseItemTrigger.UseItemTriggerInstance> {
    @Override
    public /*? if <1.20.6 {*/Codec<UseItemTriggerInstance>/*? } else*//*MapCodec<UseItemTriggerInstance>*/ codec() {
        return UseItemTriggerInstance.CODEC;
    }

    public TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, ItemStack stack) {
        return trigger(level, pos, player, instance -> instance.matches(stack), instance -> instance.beforeTrigger(level, player, stack), (instance, result) -> instance.onTrigger(result, level, player, stack));
    }

    public UseItemTriggerInstance create(ItemPredicate predicate, int damage) {
        return new UseItemTriggerInstance(predicate, damage);
    }

    public UseItemTriggerInstance create(ItemPredicate predicate) {
        return create(predicate, 0);
    }

    public UseItemTriggerInstance create(/*? if >=1.21.3*//*HolderGetter<Item> itemLookup, */Item item) {
        return create(/*? if >=1.21.3*//*itemLookup, */item, 0);

    }

    public UseItemTriggerInstance create(/*? if >=1.21.3*//*HolderGetter<Item> itemLookup, */Item item, int damage) {
        return create(ItemPredicate.Builder.item().of(/*? if >=1.21.3*//*itemLookup, */item).build(), damage);
    }

    public static class UseItemTriggerInstance extends AbstractPortalTriggerInstance<UseItemTriggerInstance> {
        //$ map_codec_swap UseItemTriggerInstance
        public static final Codec<UseItemTriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
            return itemPredicate.matches(stack);
            //? } else
            //return itemPredicate.test(stack);
        }

        public void onTrigger(TriggerResult result, Level level, @Nullable Player player, ItemStack stack) {
            if (result == TriggerResult.PASS && damageItemBy > 0 && !level.isClientSide() && (player == null || !player.getAbilities().instabuild)) {
                if (stack.isStackable()) stack.shrink(damageItemBy);
                else if (stack.isDamageableItem()) {
                    //? if <1.20.6 {
                    stack.hurt(damageItemBy, level.getRandom(), ((ServerPlayer) player));
                    //? } else if <1.21 {
                    //stack.hurtAndBreak(damageItemBy, level.getRandom(), ((ServerPlayer) player), () -> {});
                    //? } else
                    //stack.hurtAndBreak(damageItemBy, ((ServerLevel) level), ((ServerPlayer) player), item -> {});
                }
            }
        }

        public void beforeTrigger(Level level, @Nullable Player player, ItemStack stack) {
        }
    }
}
