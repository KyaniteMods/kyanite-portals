package dev.kyanitemods.kyaniteportals.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Accessor("seenCredits")
    boolean hasSeenCredits();

    @Accessor("seenCredits")
    void setSeenCredits(boolean value);
}
