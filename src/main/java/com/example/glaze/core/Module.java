package com.example.glaze.core;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name, description;
    public final Category category;
    public final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }

    protected BoolSetting bool(String n, boolean def) {
        BoolSetting s = new BoolSetting(n, def); settings.add(s); return s;
    }
    protected NumberSetting num(String n, int def, int min, int max, int step) {
        NumberSetting s = new NumberSetting(n, def, min, max, step); settings.add(s); return s;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean e) { enabled = e; }
    public void toggle() { enabled = !enabled; }
    public String keySlug() { return name.toLowerCase().replace(' ', '_'); }

    /** Called every client tick while enabled and in a world. */
    public void onTick(Minecraft mc) {}
}
