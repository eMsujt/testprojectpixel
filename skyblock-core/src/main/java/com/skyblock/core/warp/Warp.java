package com.skyblock.core.warp;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Immutable value object representing a named warp destination.
 */
public final class Warp {

    private final String name;
    private final World  world;
    private final double x;
    private final double y;
    private final double z;
    private final float  yaw;
    private final float  pitch;

    public Warp(String name, World world, double x, double y, double z, float yaw, float pitch) {
        this.name  = Objects.requireNonNull(name,  "name");
        this.world = Objects.requireNonNull(world, "world");
        this.x     = x;
        this.y     = y;
        this.z     = z;
        this.yaw   = yaw;
        this.pitch = pitch;
    }

    /** Creates a {@code Warp} from a Bukkit {@link Location}. */
    public static Warp fromLocation(String name, Location location) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(location.getWorld(), "location.world");
        return new Warp(name, location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    public String getName()  { return name;  }
    public World  getWorld() { return world; }
    public double getX()     { return x;     }
    public double getY()     { return y;     }
    public double getZ()     { return z;     }
    public float  getYaw()   { return yaw;   }
    public float  getPitch() { return pitch; }

    /** Converts this warp back to a Bukkit {@link Location}. */
    public Location toLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
