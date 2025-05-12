package me.inotsleep.utils.particle;

import me.inotsleep.utils.config.Path;
import me.inotsleep.utils.config.SerializableObject;
import me.inotsleep.utils.logging.LoggingManager;
import me.inotsleep.utils.particle.compiler.CubicBezierTransition;

import java.util.Map;

public class Frame extends SerializableObject {
    @Path("parameters")
    public Map<String, Parameter> parameters;

    @Path("duration")
    public int duration = 1;

    @Path("transition")
    private String stringTransition = "instant";

    public CubicBezierTransition transition;

    public Frame(Map<String, Parameter> parameters, int duration, String transition) {
        this.parameters = parameters;
        this.duration = duration;
        this.transition = new CubicBezierTransition(transition);
        if (duration < 0) {
            LoggingManager.error("Duration of frame must be above 0! Please recheck your configuration");
            duration = 1;
        }
    }


    public void mutateDeserialization() {
        transition = new CubicBezierTransition(stringTransition);
    }

    public void mutateSerialization() {
        stringTransition = transition.raw;
    }
}
