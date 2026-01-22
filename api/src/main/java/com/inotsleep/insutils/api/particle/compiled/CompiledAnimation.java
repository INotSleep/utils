package com.inotsleep.insutils.api.particle.compiled;

import java.util.ArrayList;
import java.util.List;

public class CompiledAnimation {
    public List<CompiledFrame> frames;

    public CompiledAnimation() {
        this.frames = new ArrayList<>();
    }

    public void drawFrame(Object player, double x, double y, double z, int frame) {
        frames.get(frame).draw(player, x, y, z);
    }
}
