package com.inotsleep.utils;

import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VectorUtil {
    public static Vector3f vectorToVector3f(Vector vec) {
        return new Vector3f((float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
    }

    public static Vector vector3fToVector(Vector3f vec) {
        return new Vector(vec.x(), vec.y(), vec.z());
    }

    public static Quaternionf toQuaterionf(Pair<Vector, Float> angle) {
        return new Quaternionf((float) angle.getK().getX(), (float) angle.getK().getY(), (float) angle.getK().getZ(), angle.getV());
    }

    public static Pair<Vector, Float> toVector(Quaternionf angle) {
        return new Pair<>(new Vector(angle.x(), angle.y(), angle.z()), angle.w());
    }
}
