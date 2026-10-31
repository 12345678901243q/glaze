package com.example.glaze.core;

import com.example.glaze.modules.*;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    public static final List<Module> ALL = new ArrayList<>();
    private Modules() {}

    public static void init() {
        if (!ALL.isEmpty()) return;
        ALL.add(new ArmorHud());
        ALL.add(new TotemTracker());
        ALL.add(new Supplies());
        ALL.add(new AttackCooldown());
        ALL.add(new Coordinates());
        ALL.add(new TerrainInfo());
        ALL.add(new Waypoints());
        ALL.add(new Radar());
        ALL.add(new ExplorationTracker());
    }
}
