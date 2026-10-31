package com.example.glaze.modules;

import com.example.glaze.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHud extends HudModule {
    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final BoolSetting hands = bool("Show hands", true);
    private final BoolSetting percent = bool("Show percent", true);

    public ArmorHud() {
        super("Armor HUD", "Durability of armor and held items.", Category.COMBAT, 4, 90);
    }

    @Override
    public void render(GuiGraphics g, Minecraft mc, int px, int py) {
        int row = 0;
        for (EquipmentSlot slot : ARMOR) row = line(g, mc, mc.player.getItemBySlot(slot), px, py, row);
        if (hands.get()) {
            row = line(g, mc, mc.player.getItemBySlot(EquipmentSlot.MAINHAND), px, py, row);
            line(g, mc, mc.player.getItemBySlot(EquipmentSlot.OFFHAND), px, py, row);
        }
    }

    private int line(GuiGraphics g, Minecraft mc, ItemStack s, int px, int py, int row) {
        if (s.isEmpty()) return row;
        int yy = py + row * 18;
        g.renderItem(s, px, yy);
        if (s.isDamageableItem()) {
            int max = s.getMaxDamage();
            int left = max - s.getDamageValue();
            double pct = max <= 0 ? 1.0 : (double) left / max;
            int color = pct > 0.6 ? 0xFF55FF55 : pct > 0.3 ? 0xFFFFFF55 : 0xFFFF5555;
            String t = percent.get() ? left + "/" + max + " (" + Math.round(pct * 100) + "%)" : left + "/" + max;
            text(g, mc, t, px + 20, yy + 5, color);
        } else if (s.getCount() > 1) {
            text(g, mc, "x" + s.getCount(), px + 20, yy + 5, 0xFFFFFFFF);
        }
        return row + 1;
    }
}
