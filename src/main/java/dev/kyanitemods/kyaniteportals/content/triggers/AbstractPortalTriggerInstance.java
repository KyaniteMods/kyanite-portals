package dev.kyanitemods.kyaniteportals.content.triggers;

public abstract class AbstractPortalTriggerInstance<I extends AbstractPortalTriggerInstance<I>> implements PortalTriggerInstance<I> {
    private final PortalTrigger<I> trigger;

    public AbstractPortalTriggerInstance(PortalTrigger<I> trigger) {
        this.trigger = trigger;
    }

    @Override
    public PortalTrigger<I> getTrigger() {
        return trigger;
    }
}
