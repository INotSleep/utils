package com.inotsleep.insutils.api.particle;

import com.inotsleep.insutils.api.config.Comment;
import com.inotsleep.insutils.api.config.Path;
import com.inotsleep.insutils.spi.config.SerializableObject;
import com.inotsleep.insutils.spi.config.UnsafeSerializableObject;
import org.bukkit.Material;
import org.bukkit.Particle;

public class Parameter extends SerializableObject {

    @Comment("Number of particles per block.")
    @Path("particlesPerBlock")
    public double particlesPerBlock;

    @Comment("X coordinate position.")
    @Path("positionX")
    public double positionX;

    @Comment("Y coordinate position.")
    @Path("positionY")
    public double positionY;

    @Comment("Z coordinate position.")
    @Path("positionZ")
    public double positionZ;

    @Comment("Offset along the X axis.")
    @Path("xo")
    public double xo;

    @Comment("Offset along the Y axis.")
    @Path("yo")
    public double yo;

    @Comment("Offset along the Z axis.")
    @Path("zo")
    public double zo;

    @Comment("X coordinate of the pivot point.")
    @Path("pivotX")
    public double pivotX;

    @Comment("Y coordinate of the pivot point.")
    @Path("pivotY")
    public double pivotY;

    @Comment("Z coordinate of the pivot point.")
    @Path("pivotZ")
    public double pivotZ;

    @Comment("Type of the particle system.")
    @Path("type")
    public Type type;

    @Comment("Rotation around the X axis.")
    @Path("rotationX")
    public double rotationX;

    @Comment("Rotation around the Y axis.")
    @Path("rotationY")
    public double rotationY;

    @Comment("Rotation around the Z axis.")
    @Path("rotationZ")
    public double rotationZ;

    @Comment("Particle type.")
    @Path("particle")
    private String particleName;
    public Particle particle;

    @Comment("Material associated with the particle.")
    @Path("data")
    private String materialName;
    public Material data;

    @Comment("Red color component (0-255).")
    @Path("r")
    public int r;

    @Comment("Green color component (0-255).")
    @Path("g")
    public int g;

    @Comment("Blue color component (0-255).")
    @Path("b")
    public int b;

    @Comment("Length/Radius/Offset length for line/circle/point.")
    @Path("l")
    public double l;

    public Parameter() {}

    public Parameter(
            double particlesPerBlock,
            double positionX,
            double positionY,
            double positionZ,
            double pivotX,
            double pivotY,
            double pivotZ,
            double xo,
            double yo,
            double zo,
            Type type,
            double rotationX,
            double rotationY,
            double rotationZ,
            Particle particle,
            Material material,
            int r,
            int g,
            int b,
            double l
    ) {
        this.particlesPerBlock = particlesPerBlock;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.pivotZ = pivotZ;
        this.xo = xo;
        this.yo = yo;
        this.zo = zo;
        this.type = type;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.particle = particle;
        this.particleName = particle.name();
        this.data = material;
        this.materialName = material.name();
        this.r = r;
        this.g = g;
        this.b = b;
        this.l = l;
    }

    public void mutateDeserialization() {
        this.particle = Particle.valueOf(particleName.toUpperCase());
        this.data = Material.matchMaterial(materialName.toUpperCase());
    }

    public void mutateSerialization() {
        this.particleName = particle.name();
        this.materialName = data.name();
    }

    public enum Type {
        point, line, circle
    }
}
