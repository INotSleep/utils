package com.inotsleep.insutils.api.particle;

import com.inotsleep.insutils.api.config.Path;
import com.inotsleep.insutils.spi.config.SerializableObject;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.api.particle.compiler.CubicBezierTransition;

import java.util.Map;

public class Frame extends SerializableObject {
    @Path("parameters")
    public Map<String, Parameter> parameters;

    @Path("duration")
    public int duration = 1;

    @Path("transition")
    public CubicBezierTransition transition;

    public Frame() {}

    public Frame(Map<String, Parameter> parameters, int duration, String transition) {
        this.parameters = parameters;
        this.duration = duration;

        double x1, y1, x2, y2;
        boolean instant = false;

        String[] parts = transition.split(",");
        if (parts.length != 4) {
            x1 = 0;
            y1 = 0;
            x2 = 0;
            y2 = 0;
            instant = true;
            return;
        }
        x1 = Double.parseDouble(parts[0]);
        y1 = Double.parseDouble(parts[1]);
        x2 = Double.parseDouble(parts[2]);
        y2 = Double.parseDouble(parts[3]);
        this.transition = new CubicBezierTransition(x1, y1, x2, y2, instant);

        if (duration < 0) {
            LoggingManager.error("Duration of frame must be above 0! Please recheck your configuration");
            this.duration = 1;
        }
    }
}
