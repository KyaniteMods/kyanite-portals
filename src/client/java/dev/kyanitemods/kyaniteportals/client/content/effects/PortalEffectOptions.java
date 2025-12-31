package dev.kyanitemods.kyaniteportals.client.content.effects;

public abstract class PortalEffectOptions<T extends PortalEffectOptions<T>> {
    public abstract PortalEffectType<T> getType();
}
