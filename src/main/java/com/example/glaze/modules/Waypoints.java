package com.example.glaze.modules;

import com.example.glaze.core.*;
import com.example.glaze.data.Waypoint;
import com.example.glaze.data.WaypointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Comparator;
import java.util.List;

public class Waypoints extends HudModule {
    private final NumberSetting maxShown = num("Max shown", 4, 1, 10, 1);

    public Waypoints() {
        super("Waypoints", "Nearest waypoints with distance and direction. Manage with /wp add|remove|list.", Category.EXPLORATION, 4, 56);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        String dim = Util.dimKey(mc.level);
        double ox = mc.player.getX(), oy = mc.player.getY(), oz = mc.player.getZ();
        List<Waypoint> list = WaypointManager.forServer(Util.serverKey(mc)).stream()
                .filter(w -> w.dim.equals(dim))
                .sorted(Comparator.comparingDouble(w -> sq(w.x - ox, w.z - oz)))
                .limit(maxShown.get())
                .toList();
        int row = 0;
        for (Waypoint w : list) {
            double dx = w.x + 0.5 - ox, dz = w.z + 0.5 - oz;
            long dist = Math.round(Math.sqrt(dx * dx + dz * dz));
            double yaw = Math.toDegrees(Math.atan2(-dx, dz));
            text(g, mc, w.name + "  " + dist + "m " + Util.compass(yaw) + "  Y" + w.y, px, py + 10 * row++, w.color);
        }
    }

    private static double sq(double a, double b) { return a * a + b * b; }
}
