package xyz.lilyflower.solaris.integration.lotr;

import com.github.bsideup.jabel.Desugar;

// Poor man's record.
// above comment no longer applies but it's funny so I'm keeping it lmao
@Desugar
@SuppressWarnings("unused")
public record LOTRFactionRankData(String name, int alignment, boolean pledge, boolean title, boolean achievement) {
}
