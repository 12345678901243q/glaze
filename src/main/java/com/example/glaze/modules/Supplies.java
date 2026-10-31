package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Items;

/** Passive inventory counters. Reads your own inventory only. */
public class Supplies extends HudModule {
    private final BoolSetting crystals = bool("Crystals", true);
    private final BoolSetting obsidian = bool("Obsidian", true);
    private final BoolSetting gapples = bool("Golden apples", true);

    public Supplies() {
        super("Supplies", "Counts of crystals, obsidian and golden apples in your inventory.", Category.COMBAT, 4, 40);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        int row = 0;
        if (crystals.get()) text(g, mc, "Crystals: " + Util.count(mc.player, Items.END_CRYSTAL), px, py + 10 * row++, 0xFFFF88FF);
        if (obsidian.get()) text(g, mc, "Obsidian: " + Util.count(mc.player, Items.OBSIDIAN), px, py + 10 * row++, 0xFFB0A0D0);
        if (gapples.get()) {
            int n = Util.count(mc.player, Items.GOLDEN_APPLE) + Util.count(mc.player, Items.ENCHANTED_GOLDEN_APPLE);
            text(g, mc, "Gapples: " + n, px, py + 10 * row, 0xFFFFD700);
        }
    }
}
