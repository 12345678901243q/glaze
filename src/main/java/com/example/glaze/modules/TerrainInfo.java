package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;

/** Only shows what your client legitimately knows: biome, light, dimension. No ore or structure finding. */
public class TerrainInfo extends HudModule {
    private String cachedBiome = "";
    private int tick = 0;

    public TerrainInfo() {
        super("Terrain Info", "Biome, light level and dimension at your position.", Category.EXPLORATION, 4, 28);
    }

    @Override
    public void onTick(Minecraft mc) {
        if (tick++ % 10 != 0) return;
        BlockPos pos = mc.player.blockPosition();
        String n = mc.level.getBiome(pos).getRegisteredName();
        int i = n.indexOf(':');
        cachedBiome = (i >= 0 ? n.substring(i + 1) : n).replace('_', ' ');
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        BlockPos pos = mc.player.blockPosition();
        text(g, mc, "Biome: " + cachedBiome, px, py, 0xFF69F0AE);
        text(g, mc, "Light: " + mc.level.getMaxLocalRawBrightness(pos) + "  Dim: " + Util.dimKey(mc.level), px, py + 10, 0xFFCFD8DC);
    }
}
