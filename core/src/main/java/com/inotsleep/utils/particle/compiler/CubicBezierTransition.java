package com.inotsleep.utils.particle.compiler;

public class CubicBezierTransition {

    private final double x1, y1, x2, y2;
    public String raw;
    private boolean instant;

    public CubicBezierTransition(String raw) {
        this.raw = raw;
        String[] parts = raw.split(",");
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
    }

    public CubicBezierTransition(double x1, double y1, double x2, double y2, boolean instant) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.instant = instant;
    }

    public double getPosition(double t) {
        if (instant) {
            if (t == 1) return 1.0;
            else return 0;
        }
        // Ensure t is within [0, 1]
        t = Math.max(0.0, Math.min(1.0, t));

        // Use Newton-Raphson method to find the t for the given x
        double x = t;
        double t0 = t;
        for (int i = 0; i < 8; i++) {
            double xCalc = cubicBezier(t0, 0, x1, x2, 1);
            double dx = cubicBezierDerivative(t0, 0, x1, x2, 1);
            if (dx == 0.0) {
                break;
            }
            double t1 = t0 - (xCalc - x) / dx;
            if (Math.abs(t1 - t0) < 1e-6) {
                t0 = t1;
                break;
            }
            t0 = t1;
        }

        // Use the solved t to get the corresponding y value
        return cubicBezier(t0, 0, y1, y2, 1);
    }

    private double cubicBezier(double t, double p0, double p1, double p2, double p3) {
        double u = 1 - t;
        return p0 * u * u * u + 3 * p1 * t * u * u + 3 * p2 * t * t * u + p3 * t * t * t;
    }

    private double cubicBezierDerivative(double t, double p0, double p1, double p2, double p3) {
        double u = 1 - t;
        return 3 * u * u * (p1 - p0) + 6 * u * t * (p2 - p1) + 3 * t * t * (p3 - p2);
    }
}