package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * This is more idiomatic java implementation of a Path object - similar to the pathing.Paths object in functionality
 * Did this way instead of a record type because that is not supported by this version of Java/Android
 * This class is immutable
 */
public class Path {
    private final double x;
    private final double y;
    private final double turn;

    public Path(double x, double y, double turn) {
        this.x = x;
        this.y = y;
        this.turn = turn;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double turn() {
        return this.turn;
    }

    @NonNull
    @Override
    public String toString() {
        return "Path{" +
                "x=" + x +
                ", y=" + y +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Double.compare(x, path.x) == 0 && Double.compare(y, path.y) == 0 && Double.compare(turn, path.turn) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, turn);
    }
}
