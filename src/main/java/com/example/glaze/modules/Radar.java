package com.example.glaze.modules;

import com.example.glaze.core.*;
import com.example.glaze.data.Waypoint;
import com.example.glaze.data.WaypointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Small rotating overlay showing your waypoints (map markers) and your recent trail. */
public class Radar extends HudModule {
    private final NumberSetting size = num("Size", 90, 40, 220, 10);
    private final NumberSetting range = num("Range (blocks)", 200, 32, 3000, 32);
    private final BoolSetting trail = bool("Show trail", true);

    public Radar() {
        super("Radar", "Top-down marker overlay of waypoints and your exploration trail.", Category.EXPLORATION, 4, 150);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        int s = size.get(), half = s / 2, cx = px + half, cy = py + half;
        Theme t = Theme.cur();
        g.fill(px - 1, py - 1, px + s + 1, py + s + 1, t.accent);
        g.fill(px, py, px + s, py + s, 0xC0101010);

        double yaw = Math.toRadians(mc.player.getYRot());
        double fx = -Math.sin(yaw), fz = Math.cos(yaw);   // forward
        double rx = -Math.cos(yaw), rz = -Math.sin(yaw);  // right
        double ox = mc.player.getX(), oz = mc.player.getZ();
        double scale = (half - 3) / (double) range.get();

        if (trail.get()) {
            for (int[] p : ExplorationTracker.recent) {
                plot(g, cx, cy, half, scale, p[0] - ox, p[1] - oz, rx, rz, fx, fz, 0x80FFFFFF, 1, false);
            }
        }
        String dim = Util.dimKey(mc.level);
        for (Waypoint w : WaypointManager.forServer(Util.serverKey(mc))) {
            if (!w.dim.equals(dim)) continue;
            plot(g, cx, cy, half, scale, w.x + 0.5 - ox, w.z + 0.5 - oz, rx, rz, fx, fz, w.color, 3, true);
        }
        g.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
        text(g, mc, "N", px + s - 8, py + 2, 0xFFAAAAAA);
    }

    private static void plot(GuiGraphics g, int cx, int cy, int half, double scale,
                             double dx, double dz, double rx, double rz, double fx, double fz,
                             int color, int dot, boolean clamp) {
        double sx = (dx * rx + dz * rz) * scale;
        double sy = (dx * fx + dz * fz) * scale;
        double lim = half - 3;
        double m = Math.max(Math.abs(sx), Math.abs(sy));
        if (m > lim) {
            if (!clamp) return;
            sx = sx / m * lim; sy = sy / m * lim;
        }
        int x = cx + (int) Math.round(sx), y = cy - (int) Math.round(sy);
        g.fill(x - dot / 2, y - dot / 2, x - dot / 2 + dot, y - dot / 2 + dot, color);
    }
}
