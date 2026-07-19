package dev.kyanitemods.kyaniteportals.content.blocks.entities;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlockEntities;
import dev.kyanitemods.kyaniteportals.util.StonecutterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomPortalBlockEntity extends BlockEntity {
    public static final Map<ResourceKey<Portal>, Integer> COLORS = new HashMap<>();

    public static final Identifier DEFAULT_KEY = KyanitePortals.id("missingno");
    private ResourceKey<Portal> portalKey = ResourceKey.create(KyanitePortals.RESOURCE_KEY, DEFAULT_KEY);

    public CustomPortalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(KyanitePortalsBlockEntities.CUSTOM_PORTAL, blockPos, blockState);
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
    //? } else if >=1.21 {
    /*protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
    *///? } else
    //protected void saveAdditional(CompoundTag tag) {
        tag.putString("portal", StonecutterUtil.getIdentifier(portalKey).toString());
    }

    @Override
    //? if >=1.21.6 {
    public void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        Identifier id = Identifier.tryParse(tag.getStringOr("portal", DEFAULT_KEY.toString()));
        if (id != null) {
            portalKey = ResourceKey.create(KyanitePortals.RESOURCE_KEY, id == null ? DEFAULT_KEY : id);
        }
    //? } else {
    /*//? if >=1.21 {
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
    //? } else {
    /^public void load(CompoundTag tag) {
        super.load(tag);
    ^///? }

        if (tag.contains("portal", net.minecraft.nbt.Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("portal"));
            if (id != null) {
                portalKey = ResourceKey.create(KyanitePortals.RESOURCE_KEY, id);
            }
        }
    *///? }

        if (hasLevel() && getLevel().isClientSide()) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public ResourceKey<Portal> getPortalKey() {
        return portalKey;
    }

    public void setPortalKey(ResourceKey<Portal> portalKey) {
        this.portalKey = portalKey;
    }

    public int getColor() {
        return COLORS.getOrDefault(getPortalKey(), 0xFFFFFF);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    //? if >=1.21 {
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
    //? } else {
    /*public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
    *///? }
}
