package dev.kyanitemods.kyaniteportals.mixin;

//? if >=1.20.6
//import com.llamalad7.mixinextras.sugar.Local;
import dev.kyanitemods.kyaniteportals.KyanitePortals;
import dev.kyanitemods.kyaniteportals.content.interfaces.EntityInPortal;
import dev.kyanitemods.kyaniteportals.content.Portal;
import dev.kyanitemods.kyaniteportals.util.KyanitePortalsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
//? if <1.21.6 {
import net.minecraft.nbt.CompoundTag;
//? } else {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;*/
//? }
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInPortal {
    @Shadow public abstract SynchedEntityData getEntityData();

    @Unique
    private int cooldown;

    @Unique
    private boolean isInsidePortal;

    @Unique
    private int portalTeleportTime;

    @Unique
    private int timeInPortal;

    @Unique
    private ResourceKey<Portal> portal;

    @Unique
    private Level portalLevel;

    @Unique
    private BlockPos portalPos;

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HAS_TRAVELED = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);

    //? if <1.20.6 {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData()V"))
    private void kyanitePortals$syncPortalData(EntityType<?> entityType, Level level, CallbackInfo ci) {
        this.getEntityData().define(DATA_HAS_TRAVELED, false);
    }
    //? } else {
    /*@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V"))
    private void kyanitePortals$syncPortalData(EntityType<?> entityType, Level level, CallbackInfo ci, @Local SynchedEntityData.Builder builder) {
        builder.define(DATA_HAS_TRAVELED, false);
    }*/
    //? }

    @Override
    public void tick(Level level, BlockPos pos, ResourceKey<Portal> portal) {
        portalLevel = level;
        portalPos = pos;
        if (getPortal() == null || !KyanitePortalsUtil.getIdentifier(portal).equals(KyanitePortalsUtil.getIdentifier(getPortal()))) {
            setPortal(portal);
            setTimeInPortal(0);
            setPortalTeleportTime(0);
            setCooldown(0);
        }

        if (getCooldown() > 0) {
            setCooldown(((Entity) (Object) this).getDimensionChangingDelay());
        } else if (getTimeInPortal() == 0 && !isInsidePortal()) {
            portalEntered(level, pos);
        }
        setInsidePortal(true);
    }

    @Unique
    private void portalEntered(Level level, BlockPos pos) {
        Optional<Portal> optional = level.registryAccess().lookupOrThrow(KyanitePortals.RESOURCE_KEY).get(getPortal()).map(Holder.Reference::value);
        optional.ifPresent(value -> {
            Portal.executeAll(level, pos, (Entity) (Object) this, value.enterActions());
        });
    }

    @Unique
    private void portalTraveled(Level level, BlockPos pos) {
        Optional<Portal> optional = level.registryAccess().lookupOrThrow(KyanitePortals.RESOURCE_KEY).get(getPortal()).map(Holder.Reference::value);
        optional.ifPresent(value -> {
            Portal.executeAll(level, pos, (Entity) (Object) this, value.travelActions());
        });
    }

    @Override
    public void setCooldown(int value) {
        cooldown = value;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void setInsidePortal(boolean value) {
        isInsidePortal = value;
    }

    @Override
    public boolean isInsidePortal() {
        return isInsidePortal;
    }

    @Override
    public void setHasTraveled(boolean value) {
        getEntityData().set(DATA_HAS_TRAVELED, value);
    }

    @Override
    public boolean hasTraveled() {
        return getEntityData().get(DATA_HAS_TRAVELED);
    }

    @Override
    public void setPortalTeleportTime(int value) {
        portalTeleportTime = value;
    }

    @Override
    public int getPortalTeleportTime() {
        return portalTeleportTime;
    }

    @Override
    public void setTimeInPortal(int value) {
        timeInPortal = value;
    }

    @Override
    public int getTimeInPortal() {
        return timeInPortal;
    }

    @Override
    public void setPortal(ResourceKey<Portal> value) {
        portal = value;
    }

    @Override
    public ResourceKey<Portal> getPortal() {
        return portal;
    }

    @Inject(method = /*? if <1.21 {*/"handleNetherPortal"/*? } else*//*"handlePortal"*/, at = @At(value = "HEAD"), cancellable = true)
    private void kyanitePortals$tickCooldown(CallbackInfo ci) {
        if (isInsidePortal()) {
            ci.cancel();
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void kyanitePortals$tick(CallbackInfo ci) {
        if (((Entity) (Object) this).level().isClientSide()) return;
        if (isInsidePortal()) {
            setTimeInPortal(getTimeInPortal() + 1);
            setPortalTeleportTime(getPortalTeleportTime() + 1);
            Optional<Portal> optional = ((Entity) (Object) this).level().registryAccess().lookupOrThrow(KyanitePortals.RESOURCE_KEY).get(getPortal()).map(Holder.Reference::value);
            if (optional.isPresent()) {
                Portal portal = optional.get();
                int teleportTime = portal.travelTime().get((Entity) (Object) this);
                if (getCooldown() == 0 && getPortalTeleportTime() >= teleportTime) {
                    setPortalTeleportTime(teleportTime);
                    setCooldown(((Entity) (Object) this).getDimensionChangingDelay());
                    if (!hasTraveled()) {
                        portalTraveled(portalLevel, portalPos);
                        if (!((Entity) (Object) this).level().isClientSide()) setHasTraveled(true);
                    }
                }
            }
            setInsidePortal(false);
            return;
        }

        if (getPortalTeleportTime() > 0) {
            setPortalTeleportTime(Math.max(getPortalTeleportTime() - 4, 0));
        }
        if (getCooldown() > 0) setCooldown(getCooldown() - 1);
        setTimeInPortal(0);

        if (getCooldown() == 0 && !((Entity) (Object) this).level().isClientSide()) setHasTraveled(false);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void kyanitePortals$restoreFrom(Entity entity, CallbackInfo ci) {
        setCooldown(((EntityInPortal) entity).getCooldown());
        setHasTraveled(((EntityInPortal) entity).hasTraveled());
        setPortalTeleportTime(((EntityInPortal) entity).getPortalTeleportTime());
        setTimeInPortal(((EntityInPortal) entity).getTimeInPortal());
        setPortal(((EntityInPortal) entity).getPortal());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/ListTag;", ordinal = 0))
    //? if <1.21.6 {
    private void kyanitePortals$addPortalData(CompoundTag tag, CallbackInfo ci) {
        setCooldown(tag.getInt("kyanite_portals:cooldown"));
        setHasTraveled(tag.getBoolean("kyanite_portals:has_traveled"));
        setPortalTeleportTime(tag.getInt("kyanite_portals:portal_teleport_time"));
        setTimeInPortal(tag.getInt("kyanite_portals:time_in_portal"));
        if (tag.contains("kyanite_portals:portal", Tag.TAG_STRING)) {
            setPortal(ResourceKey.create(KyanitePortals.RESOURCE_KEY, ResourceLocation.tryParse(tag.getString("kyanite_portals:portal"))));
        }
    }
    //? } else {
    /*private void kyanitePortals$addPortalData(ValueInput tag, CallbackInfo ci) {
        setCooldown(tag.getIntOr("kyanite_portals:cooldown", 0));
        setHasTraveled(tag.getBooleanOr("kyanite_portals:has_traveled", false));
        setPortalTeleportTime(tag.getIntOr("kyanite_portals:portal_teleport_time", 0));
        setTimeInPortal(tag.getIntOr("kyanite_portals:time_in_portal", 0));
        tag.getString("kyanite_portals:portal").ifPresent(portalString -> {
            setPortal(ResourceKey.create(KyanitePortals.RESOURCE_KEY, ResourceLocation.tryParse(portalString)));
        });
    }*/
    //? }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    //? if <1.21.6 {
    private void kyanitePortals$savePortalData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
    //? } else
    //private void kyanitePortals$savePortalData(ValueOutput tag, CallbackInfo ci) {
        tag.putInt("kyanite_portals:cooldown", getCooldown());
        tag.putBoolean("kyanite_portals:has_traveled", hasTraveled());
        tag.putInt("kyanite_portals:portal_teleport_time", getPortalTeleportTime());
        tag.putInt("kyanite_portals:time_in_portal", getTimeInPortal());
        if (getPortal() != null) {
            tag.putString("kyanite_portals:portal", KyanitePortalsUtil.getIdentifier(getPortal()).toString());
        }
    }
}
