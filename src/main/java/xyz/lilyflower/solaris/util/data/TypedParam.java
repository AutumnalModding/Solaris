package xyz.lilyflower.solaris.util.data;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record TypedParam(String type, Object value) {
    public Class<?> getType() {
        return switch(type) {
            case "int" -> int.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "boolean" -> boolean.class;
            case "long" -> long.class;
            case "string" -> String.class;
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    public Object getValue() {
        return switch(type) {
            case "int" -> ((Double) value).intValue();
            case "float" -> ((Double) value).floatValue();
            case "long" -> ((Double) value).longValue();
            default -> value;
        };
    }
}