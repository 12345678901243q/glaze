package com.example.glaze.core;

import com.example.glaze.GlazeClient;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** Profiles are JSON files in config/glaze/profiles/. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path DIR = FabricLoader.getInstance().getConfigDir().resolve("glaze");
    private static final Path PROFILES = DIR.resolve("profiles");
    public static String activeProfile = "default";

    private ConfigManager() {}

    public static String clean(String name) { return name.replaceAll("[^a-zA-Z0-9_-]", ""); }

    public static void init() {
        try { Files.createDirectories(PROFILES); } catch (IOException e) { GlazeClient.LOGGER.warn("config dir", e); }
        if (!loadProfile("default")) {
            // First run: sensible defaults, then write them out.
            for (Module m : Modules.ALL) m.setEnabled(true);
            saveProfile("default");
        }
    }

    public static void saveActive() { saveProfile(activeProfile); }

    public static boolean saveProfile(String raw) {
        String name = clean(raw);
        if (name.isEmpty()) return false;
        JsonObject root = new JsonObject();
        root.addProperty("theme", Theme.index);
        JsonObject mods = new JsonObject();
        for (Module m : Modules.ALL) {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", m.isEnabled());
            for (Setting<?> s : m.settings) {
                if (s instanceof BoolSetting b) o.addProperty(s.name, b.get());
                else if (s instanceof NumberSetting n) o.addProperty(s.name, n.get());
            }
            mods.add(m.name, o);
        }
        root.add("modules", mods);
        try {
            Files.createDirectories(PROFILES);
            Files.writeString(PROFILES.resolve(name + ".json"), GSON.toJson(root));
            activeProfile = name;
            return true;
        } catch (IOException e) {
            GlazeClient.LOGGER.warn("save profile", e);
            return false;
        }
    }

    public static boolean loadProfile(String raw) {
        String name = clean(raw);
        Path p = PROFILES.resolve(name + ".json");
        if (name.isEmpty() || !Files.exists(p)) return false;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
            if (root.has("theme")) Theme.index = Math.floorMod(root.get("theme").getAsInt(), Theme.ALL.length);
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();
            for (Module m : Modules.ALL) {
                if (!mods.has(m.name)) continue;
                JsonObject o = mods.getAsJsonObject(m.name);
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
                for (Setting<?> s : m.settings) {
                    if (!o.has(s.name)) continue;
                    if (s instanceof BoolSetting b) b.set(o.get(s.name).getAsBoolean());
                    else if (s instanceof NumberSetting n) n.set(o.get(s.name).getAsInt());
                }
            }
            activeProfile = name;
            return true;
        } catch (Exception e) {
            GlazeClient.LOGGER.warn("load profile", e);
            return false;
        }
    }

    public static List<String> listProfiles() {
        List<String> out = new ArrayList<>();
        try (Stream<Path> s = Files.list(PROFILES)) {
            s.forEach(f -> {
                String n = f.getFileName().toString();
                if (n.endsWith(".json")) out.add(n.substring(0, n.length() - 5));
            });
        } catch (IOException ignored) {}
        return out;
    }
}
