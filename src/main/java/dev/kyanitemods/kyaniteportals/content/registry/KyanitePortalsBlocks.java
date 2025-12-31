package dev.kyanitemods.kyaniteportals.content.registry;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.blocks.CustomNetherLikePortalBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KyanitePortalsBlocks {
    public static final CustomNetherLikePortalBlock CUSTOM_PORTAL = register("custom_portal", new CustomNetherLikePortalBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_PORTAL)));

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, KyanitePortals.id(id), block);
    }

    public static void load() {
        KyanitePortals.LOGGER.info("Loading blocks");
    }
}
