package xyz.lilyflower.solaris.util;

import com.github.bsideup.jabel.Desugar;

public class SolarisExtensions {
    public static final float TAU = 6.2831853F;
    public static final float PI = 3.1415927F;

    public static String basename(String name) { return name.replaceAll("\\." + extension(name), ""); }

    public static String extension(String name) { return (name.lastIndexOf('.') == -1) ? "" : name.substring(name.lastIndexOf('.') + 1); }

    @Desugar public record Pair<L, R>(L left, R right) {} // The original 927
    @Desugar public record TriPair<L, M, R>(L left, M middle, R right) {}

}
