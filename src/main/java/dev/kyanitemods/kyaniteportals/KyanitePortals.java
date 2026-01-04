package dev.kyanitemods.kyaniteportals;

import dev.kyanitemods.kyaniteportals.api.SimplePortalBuilder;
import dev.kyanitemods.kyaniteportals.content.ItemPov;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.generators.GeneratorContext;
import dev.kyanitemods.kyaniteportals.content.registry.*;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTrigger;
import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerAction;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerResult;
import io.netty.util.internal.UnstableApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///? } else
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class KyanitePortals implements ModInitializer {
    public static final String MOD_ID = "kyanite_portals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<Registry<Portal>> RESOURCE_KEY = ResourceKey.createRegistryKey(id("portal"));
    public static final Map<ResourceKey<Portal>, Function<RegistryOps.RegistryInfoLookup, Portal>> PORTAL_REGISTRY_OVERRIDES = new HashMap<>();

//    public static final Block NETHER_PORTAL = PortalHelper.registerNetherLike(rl("nether_portal"), ResourceKey.create(RESOURCE_KEY, rl("nether_portal")));
//    public static final Block END_PORTAL = PortalHelper.registerEndLike(rl("end_portal"), ResourceKey.create(RESOURCE_KEY, rl("end_portal")));

    @Override
    public void onInitialize() {
        PortalActions.load();
        KyanitePortalsParticleTypes.load();
        PortalTriggers.load();
        KyanitePortalsBlocks.load();
        KyanitePortalsBlockEntities.load();

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (player.isSpectator()) {
                //? if <1.21.2 {
                /*return InteractionResultHolder.pass(stack);
                *///? } else
                return InteractionResult.PASS;
            }

            BlockHitResult hit = ItemPov.getPlayerHitResult(world, player, ClipContext.Fluid.NONE);
            if (hit.getType() != HitResult.Type.BLOCK || !world.mayInteract(player, hit.getBlockPos()) || !player.mayUseItemAt(hit.getBlockPos(), hit.getDirection(), stack)) {
                //? if <1.21.2 {
                /*return InteractionResultHolder.pass(stack);
                *///? } else
                return InteractionResult.PASS;
            }

            BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
            if (PortalTriggers.USE_ITEM.trigger(world, pos, player, stack) == TriggerResult.FAIL) {
                //? if <1.21.2 {
                /*return InteractionResultHolder.pass(stack);
                *///? } else
                return InteractionResult.PASS;
            }
            //? if <1.21.2 {
            /*return InteractionResultHolder.success(stack);
            *///? } else
            return InteractionResult.SUCCESS;
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            reloadListeners(server.registryAccess());
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            reloadListeners(server.registryAccess());
        });

        PointOfInterestHelper.register(id("custom_portal"), 0, 1, KyanitePortalsBlocks.CUSTOM_PORTAL);
        DynamicRegistries.registerSynced(RESOURCE_KEY, Portal.CODEC);

        SimplePortalBuilder.create()
                .ignition(Items.EMERALD)
                .ignition(Blocks.EMERALD_BLOCK)
                .frame(Blocks.OBSIDIAN)
                .color(0xFF0000)
                .fromDimension(LevelStem.OVERWORLD)
                .toDimension(LevelStem.END)
                .register(id("cool_end"));
    }

    @UnstableApi
    public static void reloadListeners(RegistryAccess registryAccess) {
        PortalTriggers.REGISTRY.forEach(PortalTrigger::removeListeners);
        registryAccess.lookup(KyanitePortals.RESOURCE_KEY).ifPresent(portals -> {
            portals.listElements().map(Holder.Reference::value).forEach(portal -> {
                portal.generator().ifPresent(generator -> {
                    generator.getTriggers().forEach(trigger -> trigger.addListener(new TriggerAction() {
                        @Override
                        public <I extends PortalTriggerInstance<I>> TriggerResult run(I instance, Level level, BlockPos pos, @Nullable Player player) {
                            return generator.run(new GeneratorContext(instance, portal.tester().orElse(null), level, pos, player));
                        }
                    }));
                });
            });
        });
    }

    public static Identifier id(String path) {
        //? if <1.21 {
        /*return new ResourceLocation(MOD_ID, path);
        *///? } else
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
