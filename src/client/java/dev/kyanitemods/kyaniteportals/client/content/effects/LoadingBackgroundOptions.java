package dev.kyanitemods.kyaniteportals.client.content.effects;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public abstract class LoadingBackgroundOptions<T extends LoadingBackgroundOptions<T>> extends PortalEffectOptions<T> {
    public abstract boolean render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta);
}
