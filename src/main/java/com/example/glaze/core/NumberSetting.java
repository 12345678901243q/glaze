package com.example.glaze.core;

public class NumberSetting extends Setting<Integer> {
    public final int min, max, step;
    public NumberSetting(String name, int def, int min, int max, int step) {
        super(name, def);
        this.min = min; this.max = max; this.step = step;
    }
    @Override public void set(Integer v) { value = Math.max(min, Math.min(max, v)); }
    public void inc() { set(value + step); }
    public void dec() { set(value - step); }
}
