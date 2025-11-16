package me.inotsleep.utils.particle;

import org.bukkit.entity.Player;

public class ParticleDrawer {

    public static void draw(Object player, Parameter parameter, double posX, double posY, double posZ) {
        switch (parameter.type) {
            case point:
                drawPoint(player, parameter, posX, posY, posZ);
                break;
            case line:
                drawLine(player, parameter, posX, posY, posZ);
                break;
            case circle:
                drawCircle(player, parameter, posX, posY, posZ);
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + parameter.type);
        }
    }

    private static void drawPoint(Object player, Parameter parameter, double posX, double posY, double posZ) {
        double[] transformed = transform(
                parameter.positionX,
                parameter.positionY,
                parameter.positionZ,
                parameter
        );
        Util.drawParticle(
                player,
                posX + transformed[0],
                posY + transformed[1],
                posZ + transformed[2],
                parameter.xo,
                parameter.yo,
                parameter.zo,
                parameter.particle,
                parameter.data,
                parameter.r,
                parameter.g,
                parameter.b
        );
    }

    private static void drawLine(Object player, Parameter parameter, double posX, double posY, double posZ) {
        double length = parameter.l;
        int particles = (int) (length * parameter.particlesPerBlock);
        double dx = length / particles;

        for (int i = 0; i <= particles; i++) {
            double[] transformed = transform(
                    parameter.positionX + i * dx,
                    parameter.positionY,
                    parameter.positionZ,
                    parameter
            );
            Util.drawParticle(
                    player,
                    posX + transformed[0],
                    posY + transformed[1],
                    posZ + transformed[2],
                    parameter.xo,
                    parameter.yo,
                    parameter.zo,
                    parameter.particle,
                    parameter.data,
                    parameter.r,
                    parameter.g,
                    parameter.b
            );
        }
    }

    private static void drawCircle(Object player, Parameter parameter, double posX, double posY, double posZ) {
        int particles = (int) (2 * Math.PI * parameter.l * parameter.particlesPerBlock);
        double angleIncrement = 2 * Math.PI / particles;
        double radius = parameter.l;

        for (int i = 0; i < particles; i++) {
            double angle = i * angleIncrement;
            double x = parameter.positionX + radius * Math.cos(angle);
            double z = parameter.positionZ + radius * Math.sin(angle);
            double[] transformed = transform(
                    x,
                    parameter.positionY,
                    z,
                    parameter
            );
            Util.drawParticle(
                    player,
                    posX + transformed[0],
                    posY + transformed[1],
                    posZ + transformed[2],
                    parameter.xo,
                    parameter.yo,
                    parameter.zo,
                    parameter.particle,
                    parameter.data,
                    parameter.r,
                    parameter.g,
                    parameter.b
            );
        }
    }

    private static double[] transform(double x, double y, double z, Parameter parameter) {
        // Translate to pivot point
        x -= parameter.pivotX;
        y -= parameter.pivotY;
        z -= parameter.pivotZ;

        // Apply rotations
        double[] rotated = rotate(x, y, z, parameter.rotationX, parameter.rotationY, parameter.rotationZ);

        // Translate back from pivot point
        rotated[0] += parameter.pivotX;
        rotated[1] += parameter.pivotY;
        rotated[2] += parameter.pivotZ;

        return rotated;
    }

    private static double[] rotate(double x, double y, double z, double rotX, double rotY, double rotZ) {
        // Apply rotations in order Z, X, Y
        double[] rotatedZ = rotateAroundZ(x, y, z, rotZ);
        double[] rotatedX = rotateAroundX(rotatedZ[0], rotatedZ[1], rotatedZ[2], rotX);
        return rotateAroundY(rotatedX[0], rotatedX[1], rotatedX[2], rotY);
    }

    private static double[] rotateAroundX(double x, double y, double z, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        double newY = y * cos - z * sin;
        double newZ = y * sin + z * cos;
        return new double[]{x, newY, newZ};
    }

    private static double[] rotateAroundY(double x, double y, double z, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        double newX = x * cos + z * sin;
        double newZ = -x * sin + z * cos;
        return new double[]{newX, y, newZ};
    }

    private static double[] rotateAroundZ(double x, double y, double z, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        return new double[]{newX, newY, z};
    }
}
