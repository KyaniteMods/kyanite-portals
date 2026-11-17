package dev.kyanitemods.kyaniteportals.client.content.effects;

//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public abstract class LoadingBackgroundOptions<T extends LoadingBackgroundOptions<T>> extends PortalEffectOptions<T> {
    //~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
    public abstract boolean render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta);
}
