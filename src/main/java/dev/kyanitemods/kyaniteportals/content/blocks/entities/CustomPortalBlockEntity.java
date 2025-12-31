package dev.kyanitemods.kyaniteportals.content.blocks.entities;

import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.content.registry.KyanitePortalsBlockEntities;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.minecraft.core.BlockPos;
//? if >=1.21
//import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;*/
//? }
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomPortalBlockEntity extends BlockEntity {
    public static final Map<ResourceKey<Portal>, Integer> COLORS = new HashMap<>();

    private ResourceKey<Portal> portalKey;

    public CustomPortalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(KyanitePortalsBlockEntities.CUSTOM_PORTAL, blockPos, blockState);
    }

    @Override
    //? if >=1.21.6 {
    //protected void saveAdditional(ValueOutput tag) {
    //? } else if >=1.21 {
    //protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    //? } else
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("portal", KyanitePortalsUtil.getIdentifier(portalKey).toString());
    }

    @Override
    //? if >=1.21.6 {
    /*public void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        portalKey = ResourceKey.create(KyanitePortals.RESOURCE_KEY, ResourceLocation.tryParse(tag.getStringOr("portal", KyanitePortals.id("missingno").toString())));*/
    //? } else {
    //? if >=1.21 {
    /*public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);*/
    //? } else {
    public void load(CompoundTag tag) {
        super.load(tag);
    //? }

        if (tag.contains("portal", Tag.TAG_STRING)) {
            portalKey = ResourceKey.create(KyanitePortals.RESOURCE_KEY, ResourceLocation.tryParse(tag.getString("portal")));
        }
    //? }

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
    /*public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }*/
    //? } else {
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
    //? }
}
