package xyz.lilyflower.solaris.util.data;

import com.github.bsideup.jabel.Desugar;

@Desugar public record Pair<L, R>(L left, R right) {} // The original 927