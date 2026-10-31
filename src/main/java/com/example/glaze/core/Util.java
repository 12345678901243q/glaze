package com.example.glaze.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class Util {
    private static final String[] COMPASS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private Util() {}

    public static String serverKey(Minecraft mc) {
        ServerData sd = mc.getCurrentServer();
        return sd != null ? sd.ip.toLowerCase() : "singleplayer";
    }

    public static String dimKey(Level level) {
        if (level.dimension() == Level.NETHER) return "nether";
        if (level.dimension() == Level.END) return "end";
        return "overworld";
    }

    /** Minecraft yaw (0 = south) to a compass label. */
    public static String compass(double yaw) {
        double c = ((yaw + 180.0) % 360.0 + 360.0) % 360.0;
        return COMPASS[(int) Math.round(c / 45.0) % 8];
    }

    public static int count(Player p, Item item) {
        int n = 0;
        int size = p.getInventory().getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }

}
