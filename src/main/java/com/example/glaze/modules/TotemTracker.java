package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Items;

public class TotemTracker extends HudModule {
    private final BoolSetting alert = bool("Alert", true);

    public TotemTracker() {
        super("Totem Tracker", "Shows your totem count and warns when the offhand has none.", Category.COMBAT, 4, 70);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        int total = Util.count(mc.player, Items.TOTEM_OF_UNDYING);
        boolean offhand = mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
        int color = total == 0 ? 0xFFFF5555 : total <= 2 ? 0xFFFFFF55 : 0xFF55FF55;
        text(g, mc, "Totems: " + total, px, py, color);
        if (alert.get() && !offhand) {
            boolean flash = (System.currentTimeMillis() / 400) % 2 == 0;
            if (flash) {
                text(g, mc, total == 0 ? "OUT OF TOTEMS" : "NO TOTEM IN OFFHAND", px, py + 10, 0xFFFF5555);
            }
        }
    }
}
