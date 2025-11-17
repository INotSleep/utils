package com.inotsleep.utils.particle.compiler;

import com.inotsleep.utils.Pair;
import com.inotsleep.utils.particle.Animation;
import com.inotsleep.utils.particle.Parameter;
import com.inotsleep.utils.particle.compiled.CompiledAnimation;
import com.inotsleep.utils.particle.compiled.CompiledFrame;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Compiler {
    Map<String, List<Pair<Parameter, Pair<CubicBezierTransition, Pair<Integer, Integer>>>>> parameters;
    Animation animation;
    public Compiler(Animation animation) {
        parameters = new HashMap<>();
        this.animation = animation;
    }

    public CompiledAnimation compile() {
        AtomicInteger animationDuration = new AtomicInteger(0);

        animation.frames.forEach((kFrame, frame) -> {
            frame.parameters.forEach((key, parameter) -> {
               List<Pair<Parameter, Pair<CubicBezierTransition, Pair<Integer, Integer>>>> parameterPairs = parameters.getOrDefault(key, new ArrayList<>());
               parameterPairs.add(new Pair<>(parameter, new Pair<>(frame.transition, new Pair<>(frame.duration, animationDuration.get()))));
               parameters.put(key, parameterPairs);
           });
            animationDuration.set(frame.duration+animationDuration.get());
        });

        CompiledAnimation compiledAnimation = new CompiledAnimation();
        for (int i = 0; i < animationDuration.get(); i++) {
            compiledAnimation.frames.add(new CompiledFrame());
        }
        parameters.forEach((key, pairs) -> {
            for (int i = 0; i < pairs.size(); i++) {
                Pair<Parameter, Pair<CubicBezierTransition, Pair<Integer, Integer>>> pair1 = pairs.get(i);
                Pair<CubicBezierTransition, Pair<Integer, Integer>> pair2 = pair1.getV();
                Pair<Integer, Integer> pair3 = pair2.getV();

                Parameter parameter = pair1.getK();
                CubicBezierTransition cubicBezierTransition = pair2.getK();
                int duration = pair3.getK();
                int position = pair3.getV();

                Parameter next = pairs.size()==i+1? parameter : pairs.get(i+1).getK();

                for (int j = 0; j < duration; j++) {
                    double multiplayer = cubicBezierTransition.getPosition((double) j / duration);

                    double particlesPerBlock = (Math.abs(parameter.particlesPerBlock - next.particlesPerBlock) * multiplayer);
                    double positionX = (next.positionX - parameter.positionX) * multiplayer;
                    double positionY = (next.positionY - parameter.positionY) * multiplayer;
                    double positionZ = (next.positionZ - parameter.positionZ) * multiplayer;
                    double pivotX = (next.pivotX - parameter.pivotX) * multiplayer;
                    double pivotY = (next.pivotY - parameter.pivotY) * multiplayer;
                    double pivotZ = (next.pivotZ - parameter.pivotZ) * multiplayer;
                    double rotationX = (next.rotationX - parameter.rotationX) * multiplayer;
                    double rotationY = (next.rotationY - parameter.rotationY) * multiplayer;
                    double rotationZ = (next.rotationZ - parameter.rotationZ) * multiplayer;

                    double xo = (next.xo - parameter.xo) * multiplayer;
                    double yo = (next.yo - parameter.yo) * multiplayer;
                    double zo = (next.zo - parameter.zo) * multiplayer;

                    int r = (int) Math.round((next.r - parameter.r) * multiplayer);
                    int g = (int) Math.round((next.g - parameter.g) * multiplayer);
                    int b = (int) Math.round((next.b - parameter.b) * multiplayer);
                    double l = (next.l - parameter.l) * multiplayer;

                    compiledAnimation.frames.get(position+j).elements.add(new Parameter(
                            parameter.particlesPerBlock + particlesPerBlock,
                            parameter.positionX + positionX,
                            parameter.positionY + positionY,
                            parameter.positionZ + positionZ,
                            parameter.pivotX + pivotX,
                            parameter.pivotY + pivotY,
                            parameter.pivotZ + pivotZ,
                            parameter.xo + xo,
                            parameter.yo + yo,
                            parameter.zo + zo,
                            parameter.type,
                            parameter.rotationX + rotationX,
                            parameter.rotationY + rotationY,
                            parameter.rotationZ + rotationZ,
                            parameter.particle,
                            parameter.data,
                            parameter.r + r,
                            parameter.g + g,
                            parameter.b + b,
                            parameter.l + l
                    ));
                }
            }
        });
        return compiledAnimation;
    }
}
