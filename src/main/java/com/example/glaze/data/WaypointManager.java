package com.example.glaze.data;

import com.example.glaze.GlazeClient;
import com.example.glaze.core.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Waypoints are stored per server address in config/glaze/waypoints.json. */
public final class WaypointManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = ConfigManager.DIR.resolve("waypoints.json");
    private static final int[] PALETTE = {0xFFFF5555, 0xFF55FF55, 0xFF55AAFF, 0xFFFFFF55, 0xFFFF55FF, 0xFF55FFFF, 0xFFFFAA00};
    private static Map<String, List<Waypoint>> data = new HashMap<>();
    private static boolean loaded = false;

    private WaypointManager() {}

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        if (!Files.exists(FILE)) return;
        try {
            Map<String, List<Waypoint>> read = GSON.fromJson(Files.readString(FILE),
                    new TypeToken<Map<String, List<Waypoint>>>() {}.getType());
            if (read != null) data = read;
        } catch (Exception e) {
            GlazeClient.LOGGER.warn("waypoints load", e);
        }
    }

    public static List<Waypoint> forServer(String serverKey) {
        ensureLoaded();
        return data.computeIfAbsent(serverKey, k -> new ArrayList<>());
    }

    public static Waypoint add(String serverKey, String name, int x, int y, int z, String dim) {
        List<Waypoint> list = forServer(serverKey);
        Waypoint w = new Waypoint(name, x, y, z, dim, PALETTE[list.size() % PALETTE.length]);
        list.add(w);
        save();
        return w;
    }

    public static boolean remove(String serverKey, String name) {
        boolean r = forServer(serverKey).removeIf(w -> w.name.equalsIgnoreCase(name));
        if (r) save();
        return r;
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(data));
        } catch (IOException e) {
            GlazeClient.LOGGER.warn("waypoints save", e);
        }
    }
}
