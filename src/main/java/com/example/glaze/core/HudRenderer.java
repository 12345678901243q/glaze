package com.example.glaze.core;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(GuiGraphics g, DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        for (Module m : Modules.ALL) {
            if (m.isEnabled() && m instanceof HudModule h) {
                h.render(g, mc, h.x.get(), h.y.get());
            }
        }
    }
}
