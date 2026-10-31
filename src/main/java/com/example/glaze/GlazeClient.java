package com.example.glaze;

import com.example.glaze.core.*;
import com.example.glaze.data.ExplorationData;
import com.example.glaze.data.Waypoint;
import com.example.glaze.data.WaypointManager;
import com.example.glaze.gui.ClientScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GlazeClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("glaze");
    private static final Map<Module, KeyMapping> MODULE_KEYS = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        Modules.init();
        ConfigManager.init();

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("glaze", "main"));
        KeyMapping openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.glaze.open_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        for (Module m : Modules.ALL) {
            // Unbound by default; bind them in Options > Controls (or the Keybinds button in the GUI).
            MODULE_KEYS.put(m, KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.glaze.module." + m.keySlug(), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category)));
        }

        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath("glaze", "hud"), HudRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) {
                if (mc.screen == null) mc.setScreen(new ClientScreen(null));
            }
            for (Map.Entry<Module, KeyMapping> e : MODULE_KEYS.entrySet()) {
                while (e.getValue().consumeClick()) e.getKey().toggle();
            }
            if (mc.player != null && mc.level != null) {
                for (Module m : Modules.ALL) if (m.isEnabled()) m.onTick(mc);
            }
        });

        // Auto-load a profile named "donutsmp" when joining a matching server, if you created one.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, mc) -> {
            if (Util.serverKey(mc).contains("donutsmp") && ConfigManager.listProfiles().contains("donutsmp")) {
                ConfigManager.loadProfile("donutsmp");
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            ConfigManager.saveActive();
            ExplorationData.saveIfDirty();
        });

        registerCommands();
        LOGGER.info("Glaze Client loaded with {} modules", Modules.ALL.size());
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("wp")
                .then(ClientCommandManager.literal("add")
                    .then(ClientCommandManager.argument("name", StringArgumentType.word()).executes(ctx -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.player == null || mc.level == null) return 0;
                        String name = StringArgumentType.getString(ctx, "name");
                        Waypoint w = WaypointManager.add(Util.serverKey(mc), name,
                                mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ(), Util.dimKey(mc.level));
                        ctx.getSource().sendFeedback(Component.literal(
                                "Waypoint '" + w.name + "' added at " + w.x + " " + w.y + " " + w.z));
                        return 1;
                    })))
                .then(ClientCommandManager.literal("remove")
                    .then(ClientCommandManager.argument("name", StringArgumentType.word()).executes(ctx -> {
                        Minecraft mc = Minecraft.getInstance();
                        String name = StringArgumentType.getString(ctx, "name");
                        boolean ok = WaypointManager.remove(Util.serverKey(mc), name);
                        ctx.getSource().sendFeedback(Component.literal(ok ? "Removed '" + name + "'" : "No waypoint named '" + name + "'"));
                        return ok ? 1 : 0;
                    })))
                .then(ClientCommandManager.literal("list").executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    List<Waypoint> list = WaypointManager.forServer(Util.serverKey(mc));
                    if (list.isEmpty()) ctx.getSource().sendFeedback(Component.literal("No waypoints saved for this server."));
                    for (Waypoint w : list) {
                        ctx.getSource().sendFeedback(Component.literal(
                                w.name + ": " + w.x + " " + w.y + " " + w.z + " (" + w.dim + ")"));
                    }
                    return list.size();
                }))
        ));
    }
}
