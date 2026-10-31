package com.example.glaze.core;

public class Theme {
    public final String name;
    public final int accent, panel, text;
    public Theme(String name, int accent, int panel, int text) {
        this.name = name; this.accent = accent; this.panel = panel; this.text = text;
    }
    public static final Theme[] ALL = {
        new Theme("Ocean",  0xFF4FC3F7, 0xF0111A24, 0xFFE6EEF5),
        new Theme("Violet", 0xFFB388FF, 0xF01A1426, 0xFFEEE6FF),
        new Theme("Forest", 0xFF69F0AE, 0xF0101E18, 0xFFE6F5EC),
        new Theme("Ember",  0xFFFF8A65, 0xF0241612, 0xFFF5EAE6),
        new Theme("Mono",   0xFFCFD8DC, 0xF01B1B1B, 0xFFFFFFFF)
    };
    public static int index = 0;
    public static Theme cur() { return ALL[index % ALL.length]; }
    public static void next() { index = (index + 1) % ALL.length; }
}
