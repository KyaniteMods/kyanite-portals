package dev.kyanitemods.kyaniteportals.content.registry;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.blocks.entities.CustomPortalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class KyanitePortalsBlockEntities {
    public static final BlockEntityType<CustomPortalBlockEntity> CUSTOM_PORTAL = register("custom_portal", FabricBlockEntityTypeBuilder.create(
            CustomPortalBlockEntity::new,
            KyanitePortalsBlocks.CUSTOM_PORTAL
    ).build());

    private static <B extends BlockEntity, T extends BlockEntityType<B>> T register(String id, T type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KyanitePortals.id(id), type);
    }

    public static void load() {
        KyanitePortals.LOGGER.info("Loading block entities");
    }
}
