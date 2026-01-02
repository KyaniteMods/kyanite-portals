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
    //? if <1.21.3 {
    /*public static final CustomNetherLikePortalBlock CUSTOM_PORTAL = register("custom_portal", new CustomNetherLikePortalBlock());

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, KyanitePortals.id(id), block);
    }*/
    //? } else {
    public static final CustomNetherLikePortalBlock CUSTOM_PORTAL = (CustomNetherLikePortalBlock) register("custom_portal", CustomNetherLikePortalBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_PORTAL));

    private static Block register(String id, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return Blocks.register(ResourceKey.create(Registries.BLOCK, KyanitePortals.id(id)), function, properties);
    }
    //? }

    public static void load() {
        KyanitePortals.LOGGER.info("Loading blocks");
    }
}
