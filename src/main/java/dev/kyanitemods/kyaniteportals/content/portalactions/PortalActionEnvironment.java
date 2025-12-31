package dev.kyanitemods.kyaniteportals.content.portalactions;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PortalActionEnvironment implements StringRepresentable {
    CLIENT("client", true, false),
    SERVER("server", false, true),
    COMMON("common", true, true);

    public static Codec<PortalActionEnvironment> CODEC = StringRepresentable.fromEnum(PortalActionEnvironment::values);

    private final String name;
    private final boolean client;
    private final boolean server;

    PortalActionEnvironment(String name, boolean client, boolean server) {
        this.name = name;
        this.client = client;
        this.server = server;
    }

    public boolean isClient() {
        return client;
    }

    public boolean isServer() {
        return server;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
