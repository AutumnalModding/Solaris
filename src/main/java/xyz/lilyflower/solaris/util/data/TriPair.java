package xyz.lilyflower.solaris.util.data;

import com.github.bsideup.jabel.Desugar;

@Desugar public record TriPair<L, M, R>(L left, M middle, R right) {}
