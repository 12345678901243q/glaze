package com.example.glaze.modules;

import com.example.glaze.core.*;
import com.example.glaze.data.ExplorationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayDeque;
import java.util.Deque;

/** Records which 16x16 chunks you have visited (saved per server and dimension). */
public class ExplorationTracker extends HudModule {
    public static final Deque<int[]> recent = new ArrayDeque<>();
    private static String lastDim = "";
    private int tick = 0, sessionNew = 0, total = 0;

    public ExplorationTracker() {
        super("Exploration Tracker", "Counts chunks you have explored on this server.", Category.EXPLORATION, 4, 250);
    }

    @Override
    public void onTick(Minecraft mc) {
        if (++tick % 10 != 0) return;
        String server = Util.serverKey(mc), dim = Util.dimKey(mc.level);
        if (!dim.equals(lastDim)) { recent.clear(); lastDim = dim; }
        int bx = mc.player.getBlockX(), bz = mc.player.getBlockZ();
        if (ExplorationData.visit(server, dim, bx >> 4, bz >> 4)) sessionNew++;
        total = ExplorationData.chunks(server, dim).size();

        int[] last = recent.peekLast();
        if (last == null || Math.abs(last[0] - bx) + Math.abs(last[1] - bz) >= 8) {
            recent.addLast(new int[]{bx, bz});
            if (recent.size() > 300) recent.pollFirst();
        }
        if (tick % 2400 == 0) ExplorationData.saveIfDirty();
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        text(g, mc, "Explored: " + total + " chunks (+" + sessionNew + " this session)", px, py, 0xFF4FC3F7);
    }
}
