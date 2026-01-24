package xyz.lilyflower.solaris.util;

import com.github.bsideup.jabel.Desugar;
import java.util.List;
import java.util.Random;

// all stolen from efr lmao
@SuppressWarnings("unused")
public class SolarisExtensions {
    public static final float TAU = 6.2831853F;
    public static final float PI = 3.1415927F;

    public static String basename(String name) { return name.replaceAll("\\." + extension(name), ""); }
    public static String extension(String name) { return (name.lastIndexOf('.') == -1) ? "" : name.substring(name.lastIndexOf('.') + 1); }

    @Desugar public record Pair<L, R>(L left, R right) {} // The original 927
    @Desugar public record TriPair<L, M, R>(L left, M middle, R right) {}

    public static long lfloor(double value) {
        long l = (long) value;
        return value < (double) l ? l - 1L : l;
    }

    public static <T> T getRandom(List<T> list, Random rand) {
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> T getRandom(T[] array, Random rand) {
        return array[rand.nextInt(array.length)];
    }

    public static int getRandom(int[] array, Random rand) {
        return array[rand.nextInt(array.length)];
    }

    /**
     * A three-dimensional lerp between values on the 8 corners of the unit cube.
     * Arbitrary values are specified for the corners and the output is interpolated
     * between them.
     *
     * @param deltaX the x-coordinate on the unit cube
     * @param deltaY the y-coordinate on the unit cube
     * @param deltaZ the z-coordinate on the unit cube
     * @param x0y0z0 the output if {@code deltaX} is 0, {@code deltaY} is 0 and
     *               {@code deltaZ} is 0
     * @param x1y0z0 the output if {@code deltaX} is 1, {@code deltaY} is 0 and
     *               {@code deltaZ} is 0
     * @param x0y1z0 the output if {@code deltaX} is 0, {@code deltaY} is 1 and
     *               {@code deltaZ} is 0
     * @param x1y1z0 the output if {@code deltaX} is 1, {@code deltaY} is 1 and
     *               {@code deltaZ} is 0
     * @param x0y0z1 the output if {@code deltaX} is 0, {@code deltaY} is 0 and
     *               {@code deltaZ} is 1
     * @param x1y0z1 the output if {@code deltaX} is 1, {@code deltaY} is 0 and
     *               {@code deltaZ} is 1
     * @param x0y1z1 the output if {@code deltaX} is 0, {@code deltaY} is 1 and
     *               {@code deltaZ} is 1
     * @param x1y1z1 the output if {@code deltaX} is 1, {@code deltaY} is 1 and
     *               {@code deltaZ} is 1
     */

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0,
                               double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0),
                lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    /**
     * A two-dimensional lerp between values on the 4 corners of the unit square.
     * Arbitrary values are specified for the corners and the output is interpolated
     * between them.
     *
     * @param deltaX the x-coordinate on the unit square
     * @param deltaY the y-coordinate on the unit square
     * @param x0y0   the output if {@code deltaX} is 0 and {@code deltaY} is 0
     * @param x1y0   the output if {@code deltaX} is 1 and {@code deltaY} is 0
     * @param x0y1   the output if {@code deltaX} is 0 and {@code deltaY} is 1
     * @param x1y1   the output if {@code deltaX} is 1 and {@code deltaY} is 1
     */
    public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
    }

    public static double perlinFade(double value) {
        return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
    }

    public static double perlinFadeDerivative(double value) {
        return 30.0D * value * value * (value - 1.0D) * (value - 1.0D);
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static float invSqrt(float num) {
        return 1 / (float) Math.sqrt(num);
    }

    public static double invSqrt(double num) {
        return 1 / Math.sqrt(num);
    }
}
