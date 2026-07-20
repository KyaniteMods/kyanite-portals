package dev.kyanitemods.kyaniteportals.mixin;

import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContextAwarePredicate.class)
public interface ContextAwarePredicateAccessor {
    //? if <1.20.4 {
    /*@Accessor("conditions")
    LootItemCondition[] kyanitePortals$getConditions();
    *///? }
}
