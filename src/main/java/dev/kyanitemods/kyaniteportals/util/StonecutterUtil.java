package dev.kyanitemods.kyaniteportals.util;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class StonecutterUtil {
    public static Identifier getIdentifier(ResourceKey<?> resourceKey) {
        //? if <1.21.11 {
        /*return resourceKey.location();
         *///? } else
        return resourceKey.identifier();
    }
}
