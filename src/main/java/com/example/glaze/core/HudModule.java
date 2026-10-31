package com.example.glaze.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class HudModule extends Module {
    public final NumberSetting x, y;

    protected HudModule(String name, String description, Category category, int defX, int defY) {
        super(name, description, category);
        x = num("X", defX, 0, 4000, 4);
        y = num("Y", defY, 0, 4000, 4);
    }

    /** Draw at the configured position. Must not allocate heavily; runs every frame. */
    public abstract void render(GuiGraphics g, Minecraft mc, int px, int py);

    protected static void text(GuiGraphics g, Minecraft mc, String s, int px, int py, int color) {
        g.drawString(mc.font, s, px, py, color, true);
    }
}
