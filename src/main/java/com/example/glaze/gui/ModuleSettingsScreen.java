package com.example.glaze.gui;

import com.example.glaze.core.*;
import com.example.glaze.core.Module;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;
    private int px, py, pw, ph;

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Component.literal(module.name + " settings"));
        this.parent = parent;
        this.module = module;
    }

    @Override
    protected void init() {
        pw = Math.min(width - 16, 300);
        ph = Math.min(height - 16, 40 + Math.max(1, module.settings.size()) * 24 + 34);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        int y = py + 28;
        for (Setting<?> s : module.settings) {
            if (s instanceof BoolSetting b) {
                addRenderableWidget(Button.builder(boolLabel(b), btn -> { b.toggle(); btn.setMessage(boolLabel(b)); })
                        .bounds(px + 10, y, pw - 20, 20).build());
            } else if (s instanceof NumberSetting n) {
                Button label = Button.builder(numLabel(n), btn -> {}).bounds(px + 34, y, pw - 68, 20).build();
                label.active = false;
                addRenderableWidget(Button.builder(Component.literal("-"), btn -> { n.dec(); label.setMessage(numLabel(n)); })
                        .bounds(px + 10, y, 20, 20).build());
                addRenderableWidget(label);
                addRenderableWidget(Button.builder(Component.literal("+"), btn -> { n.inc(); label.setMessage(numLabel(n)); })
                        .bounds(px + pw - 30, y, 20, 20).build());
            }
            y += 24;
        }
        addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose())
                .bounds(px + pw / 2 - 40, py + ph - 26, 80, 18).build());
    }

    private static Component boolLabel(BoolSetting b) {
        return Component.literal(b.name + ": ").append(b.get()
                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("OFF").withStyle(ChatFormatting.RED));
    }

    private static Component numLabel(NumberSetting n) { return Component.literal(n.name + ": " + n.get()); }

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
        g.drawString(font, module.name, px + 10, py + 10, Theme.cur().accent, true);
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() {
        ConfigManager.saveActive();
        minecraft.setScreen(parent);
    }
}
