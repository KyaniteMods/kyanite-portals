package dev.kyanitemods.kyaniteportals.content.registry;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.blocks.CustomNetherLikePortalBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;

public class KyanitePortalsBlocks {
    public static final CustomNetherLikePortalBlock CUSTOM_PORTAL = (CustomNetherLikePortalBlock) register("custom_portal", CustomNetherLikePortalBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_PORTAL));

    private static Block register(String id, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        //? if <1.21.3 {
        /*return Registry.register(BuiltInRegistries.BLOCK, KyanitePortals.id(id), function.apply(properties));
        *///? } else
        return Blocks.register(ResourceKey.create(Registries.BLOCK, KyanitePortals.id(id)), function, properties);
    }

    public static void load() {
        KyanitePortals.LOGGER.info("Loading blocks");
    }
}
