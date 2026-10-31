package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

public class Coordinates extends HudModule {
    private final BoolSetting facing = bool("Show facing", true);
    private final BoolSetting converted = bool("Nether/Overworld conversion", true);

    public Coordinates() {
        super("Coordinates", "Your position, facing direction and Nether/Overworld equivalent.", Category.EXPLORATION, 4, 4);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        int bx = mc.player.getBlockX(), by = mc.player.getBlockY(), bz = mc.player.getBlockZ();
        String dir = facing.get() ? "  " + Util.compass(mc.player.getYRot()) : "";
        text(g, mc, "XYZ: " + bx + " " + by + " " + bz + dir, px, py, 0xFFFFFFFF);
        if (converted.get()) {
            if (mc.level.dimension() == Level.OVERWORLD) {
                text(g, mc, "Nether: " + (bx / 8) + " " + (bz / 8), px, py + 10, 0xFFFF8A65);
            } else if (mc.level.dimension() == Level.NETHER) {
                text(g, mc, "Overworld: " + (bx * 8) + " " + (bz * 8), px, py + 10, 0xFF69F0AE);
            }
        }
    }
}
