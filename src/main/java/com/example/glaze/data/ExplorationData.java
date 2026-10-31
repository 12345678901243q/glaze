package com.example.glaze.data;

import com.example.glaze.GlazeClient;
import com.example.glaze.core.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Visited chunks per server and dimension (the "exploration history"). */
public final class ExplorationData {
    private static final Gson GSON = new Gson();
    private static final Path FILE = ConfigManager.DIR.resolve("exploration.json");
    private static Map<String, Map<String, Set<Long>>> data = new HashMap<>();
    private static boolean loaded = false;
    private static boolean dirty = false;

    private ExplorationData() {}

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        if (!Files.exists(FILE)) return;
        try {
            Map<String, Map<String, Set<Long>>> read = GSON.fromJson(Files.readString(FILE),
                    new TypeToken<Map<String, Map<String, Set<Long>>>>() {}.getType());
            if (read != null) data = read;
        } catch (Exception e) {
            GlazeClient.LOGGER.warn("exploration load", e);
        }
    }

    public static Set<Long> chunks(String server, String dim) {
        ensureLoaded();
        return data.computeIfAbsent(server, k -> new HashMap<>()).computeIfAbsent(dim, k -> new HashSet<>());
    }

    public static long key(int cx, int cz) { return ((long) cx << 32) | (cz & 0xFFFFFFFFL); }

    public static boolean visit(String server, String dim, int cx, int cz) {
        boolean added = chunks(server, dim).add(key(cx, cz));
        if (added) dirty = true;
        return added;
    }

    public static void saveIfDirty() {
        if (!dirty) return;
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(data));
            dirty = false;
        } catch (IOException e) {
            GlazeClient.LOGGER.warn("exploration save", e);
        }
    }
}
