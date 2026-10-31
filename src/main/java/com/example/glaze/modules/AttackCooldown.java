package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Shows your own attack-strength meter (what the vanilla crosshair indicator shows).
 * Vanilla end crystals have no use cooldown, so this is the cooldown that matters in PvP timing.
 */
public class AttackCooldown extends HudModule {
    private final NumberSetting width = num("Width", 60, 20, 200, 4);

    public AttackCooldown() {
        super("Attack Cooldown", "Bar showing your weapon's attack charge.", Category.COMBAT, 4, 120);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        float s = Math.max(0f, Math.min(1f, mc.player.getAttackStrengthScale(0f)));
        int w = width.get();
        g.fill(px - 1, py - 1, px + w + 1, py + 7, 0xA0000000);
        g.fill(px, py, px + Math.round(w * s), py + 6, s >= 1f ? 0xFF55FF55 : 0xFFFFAA00);
        text(g, mc, Math.round(s * 100) + "%", px + w + 4, py - 1, 0xFFFFFFFF);
    }
}
