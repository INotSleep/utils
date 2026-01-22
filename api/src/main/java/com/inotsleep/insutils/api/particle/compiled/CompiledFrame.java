package com.inotsleep.insutils.api.particle.compiled;

import com.inotsleep.insutils.api.particle.Parameter;
import com.inotsleep.insutils.api.particle.ParticleDrawer;

import java.util.ArrayList;
import java.util.List;

public class CompiledFrame {
    public List<Parameter> elements;
    public CompiledFrame() {
        this.elements = new ArrayList<>();
    }
    public void draw(Object player, double x, double y, double z) {
        elements.forEach(e -> ParticleDrawer.draw(player, e, x, y, z));
    }
}
