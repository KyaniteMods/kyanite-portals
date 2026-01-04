package dev.kyanitemods.kyaniteportals.content.generators;

import dev.kyanitemods.kyaniteportals.content.triggers.PortalTriggerInstance;
import dev.kyanitemods.kyaniteportals.content.triggers.TriggerResult;

import java.util.List;

public abstract class PortalGenerator<T extends PortalGenerator<T>> {
    public abstract PortalGeneratorType<T> getType();
    public abstract List<PortalTriggerInstance<?>> getTriggers();
    public abstract TriggerResult run(GeneratorContext context);
}
