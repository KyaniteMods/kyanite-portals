package dev.kyanitemods.kyaniteportals.content.triggers;

import com.mojang.serialization.Codec;
//? if >=1.20.6
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kyanitemods.kyaniteportals.content.registry.PortalTriggers;
import dev.kyanitemods.kyaniteportals.util.CodecHelper;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
//? if >=1.21.3
import net.minecraft.core.HolderGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//? if <1.21.5 {
/*import net.minecraft.world.entity.projectile.ThrownPotion;
*///? } else if <1.21.11 {
/*import net.minecraft.world.entity.projectile.AbstractThrownPotion;
*///? } else
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
//? if >=1.20.6 {
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
//? } else
//import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ThrownPotionTrigger extends SimplePortalTrigger<ThrownPotionTrigger.ThrownPotionTriggerInstance> {
    @Override
    public /*? if <1.20.6 {*//*Codec<ThrownPotionTriggerInstance>*//*? } else {*/MapCodec<ThrownPotionTriggerInstance>/*? }*/ codec() {
        return ThrownPotionTriggerInstance.CODEC;
    }

    public TriggerResult trigger(Level level, BlockPos pos, @Nullable Player player, /*? if <1.21.5 {*//*ThrownPotion*//*? } else { */AbstractThrownPotion/*? }*/ potion) {
        return trigger(level, pos, player, instance -> instance.matches(potion), (instance) -> instance.beforeTrigger(level, pos, player), (instance, result) -> instance.onTrigger(result, level, pos, player));
    }

    public ThrownPotionTriggerInstance create(List<Holder<Potion>> potions) {
        return new ThrownPotionTriggerInstance(potions, Optional.empty());
    }

    public ThrownPotionTriggerInstance create(List<Holder<Potion>> potions, ItemPredicate predicate) {
        return new ThrownPotionTriggerInstance(potions, Optional.of(predicate));
    }

    public ThrownPotionTriggerInstance create(List<Holder<Potion>> potions, /*? if >=1.21.3 {*/HolderGetter<Item> itemLookup, /*? }*/Item item) {
        return create(potions, ItemPredicate.Builder.item().of(/*? if >=1.21.3 {*/itemLookup, /*? }*/item).build());
    }

    public static class ThrownPotionTriggerInstance extends AbstractPortalTriggerInstance<ThrownPotionTriggerInstance> {
        //$ map_codec_swap ThrownPotionTriggerInstance
        public static final MapCodec<ThrownPotionTriggerInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CodecHelper.POTION_CODEC.listOf().fieldOf("potions").forGetter(i -> i.potions),
                CodecHelper.ITEM_PREDICATE_CODEC.optionalFieldOf("predicate").forGetter(i -> i.itemPredicate)
        ).apply(instance, ThrownPotionTriggerInstance::new));

        private final List<Holder<Potion>> potions;
        private final Optional<ItemPredicate> itemPredicate;

        public ThrownPotionTriggerInstance(List<Holder<Potion>> potions, Optional<ItemPredicate> itemPredicate) {
            super(PortalTriggers.THROWN_POTION);
            this.potions = potions;
            this.itemPredicate = itemPredicate;
        }

        public boolean matches(/*? if <1.21.5 {*//*ThrownPotion*//*? } else { */AbstractThrownPotion/*? }*/ entity) {
            ItemStack stack = entity.getItem();
            if (itemPredicate.isPresent() && /*? if <1.20.6 {*//*!itemPredicate.get().matches(stack)*//*? } else {*/!itemPredicate.get().test(stack)/*? }*/) return false;

            if (potions.isEmpty()) return true;

            //? if <1.20.6 {
            /*Potion potion = PotionUtils.getPotion(stack);
            return potions.size() == 1 && potions.get(0).value() == potion;
            *///? } else {
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            for (Holder<Potion> holder : this.potions) {
                if (!contents.is(holder)) return false;
            }
            return true;
            //? }
        }

        public void onTrigger(TriggerResult result, Level level, BlockPos pos, @Nullable Entity entity) {
        }

        public void beforeTrigger(Level level, BlockPos pos, @Nullable Entity entity) {
        }
    }
}
