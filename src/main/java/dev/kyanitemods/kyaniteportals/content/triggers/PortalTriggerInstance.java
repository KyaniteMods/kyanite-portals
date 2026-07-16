package dev.kyanitemods.kyaniteportals.content.triggers;

public interface PortalTriggerInstance<I extends PortalTriggerInstance<I>> {
    PortalTrigger<I> getTrigger();

    @SuppressWarnings("unchecked")
    default void addListener(TriggerAction action) {
        PortalTrigger<I> trigger = getTrigger();
        trigger.addListener(new PortalTrigger.Listener<>((I) this, action));
    }
}
