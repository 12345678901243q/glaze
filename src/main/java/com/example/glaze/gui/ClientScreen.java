package com.example.glaze.gui;

import com.example.glaze.core.*;
import com.example.glaze.core.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientScreen extends Screen {
    private static final int ROW_H = 22;
    private final Screen parent;
    private String query = "";
    private Category cat = null;
    private int scroll = 0;
    private String status = "";
    private String profileText = ConfigManager.activeProfile;
    private EditBox searchBox, profileBox;
    private int px, py, pw, ph, listTop, rows;
    private List<Module> visible = List.of();

    public ClientScreen(Screen parent) {
        super(Component.literal("Glaze Client"));
        this.parent = parent;
    }

    private List<Module> filtered() {
        String q = query.toLowerCase();
        return Modules.ALL.stream()
                .filter(m -> cat == null || m.category == cat)
                .filter(m -> q.isEmpty() || m.name.toLowerCase().contains(q))
                .toList();
    }

    @Override
    protected void init() {
        pw = Math.min(width - 16, 460);
        ph = Math.min(height - 16, 300);
        px = (width - pw) / 2;
        py = (height - ph) / 2;

        // Category tabs
        Category[] cats = Category.values();
        int tabs = cats.length + 1, tw = (pw - 16) / tabs;
        for (int i = 0; i < tabs; i++) {
            final Category c = i == 0 ? null : cats[i - 1];
            String label = c == null ? "All" : c.label;
            Component msg = Component.literal(label);
            if (c == cat) msg = Component.literal(label).withStyle(ChatFormatting.UNDERLINE);
            addRenderableWidget(Button.builder(msg, b -> { cat = c; scroll = 0; rebuildWidgets(); })
                    .bounds(px + 8 + i * tw, py + 22, tw - 2, 18).build());
        }

        // Search
        searchBox = new EditBox(font, px + 8, py + 44, pw - 16, 16, Component.literal("Search"));
        searchBox.setHint(Component.literal("Search modules..."));
        searchBox.setValue(query);
        searchBox.setResponder(s -> { query = s; scroll = 0; rebuildWidgets(); });
        addRenderableWidget(searchBox);
        if (!query.isEmpty()) setFocused(searchBox);

        // Module list
        listTop = py + 66;
        int listBottom = py + ph - 52;
        rows = Math.max(1, (listBottom - listTop) / ROW_H);
        List<Module> all = filtered();
        scroll = Math.max(0, Math.min(scroll, Math.max(0, all.size() - rows)));
        visible = all.subList(scroll, Math.min(all.size(), scroll + rows));
        for (int i = 0; i < visible.size(); i++) {
            final Module m = visible.get(i);
            int y = listTop + i * ROW_H;
            Button toggle = Button.builder(toggleLabel(m), b -> { m.toggle(); b.setMessage(toggleLabel(m)); })
                    .bounds(px + 8, y, 170, 20).build();
            addRenderableWidget(toggle);
            addRenderableWidget(Button.builder(Component.literal("..."), b -> minecraft.setScreen(new ModuleSettingsScreen(this, m)))
                    .bounds(px + 182, y, 22, 20).build());
        }

        // Profiles
        int y1 = py + ph - 46, y2 = py + ph - 24;
        profileBox = new EditBox(font, px + 8, y1, 110, 16, Component.literal("Profile"));
        profileBox.setValue(profileText);
        profileBox.setResponder(s -> profileText = s);
        addRenderableWidget(profileBox);
        addRenderableWidget(Button.builder(Component.literal("Save"), b -> {
            status = ConfigManager.saveProfile(profileText) ? "Saved profile '" + ConfigManager.activeProfile + "'" : "Could not save";
        }).bounds(px + 122, y1 - 1, 44, 18).build());
        addRenderableWidget(Button.builder(Component.literal("Load"), b -> {
            if (ConfigManager.loadProfile(profileText)) { status = "Loaded '" + ConfigManager.activeProfile + "'"; rebuildWidgets(); }
            else status = "No profile named '" + ConfigManager.clean(profileText) + "'";
        }).bounds(px + 170, y1 - 1, 44, 18).build());

        addRenderableWidget(Button.builder(Component.literal("Theme: " + Theme.cur().name), b -> {
            Theme.next(); rebuildWidgets();
        }).bounds(px + 8, y2, 120, 18).build());
        addRenderableWidget(Button.builder(Component.literal("Keybinds"), b ->
                minecraft.setScreen(new KeyBindsScreen(this, minecraft.options))).bounds(px + 132, y2, 90, 18).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(px + pw - 68, y2, 60, 18).build());
    }

    private static Component toggleLabel(Module m) {
        return Component.literal(m.name + ": ").append(m.isEnabled()
                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("OFF").withStyle(ChatFormatting.RED));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        int max = Math.max(0, filtered().size() - rows);
        int next = Math.max(0, Math.min(max, scroll - (int) Math.signum(sy)));
        if (next != scroll) { scroll = next; rebuildWidgets(); }
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float pt) {
        Theme t = Theme.cur();
        g.fill(0, 0, width, height, 0x90000000);
        g.fill(px, py, px + pw, py + ph, t.panel);
        g.fill(px, py, px + pw, py + 2, t.accent);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        Theme t = Theme.cur();
        g.drawString(font, "Glaze Client", px + 8, py + 8, t.accent, true);
        g.drawString(font, "Profile: " + ConfigManager.activeProfile, px + pw - 8 - font.width("Profile: " + ConfigManager.activeProfile), py + 8, t.text, true);
        for (int i = 0; i < visible.size(); i++) {
            String d = font.plainSubstrByWidth(visible.get(i).description, pw - 220);
            g.drawString(font, d, px + 210, listTop + i * ROW_H + 6, 0xFF9AA5B1, false);
        }
        if (filtered().isEmpty()) g.drawString(font, "No modules match.", px + 12, listTop + 6, 0xFF9AA5B1, false);
        if (!status.isEmpty()) g.drawString(font, status, px + 222, py + ph - 42, t.text, false);
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() {
        ConfigManager.saveActive();
        minecraft.setScreen(parent);
    }
}
