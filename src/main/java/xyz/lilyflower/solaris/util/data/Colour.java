package xyz.lilyflower.solaris.util.data;


import com.github.bsideup.jabel.Desugar;

@Desugar
public record Colour(float red, float green, float blue) {
    public static Colour fromHex(String hex) {
        hex = hex.replace("#", "");
        int rgb = Integer.parseInt(hex, 16);

        return new Colour(
                ((rgb >> 16) & 0xFF) / 255.0f,
                ((rgb >> 8) & 0xFF) / 255.0f,
                (rgb & 0xFF) / 255.0f
        );
    }

    public String toHex() {
        int red = (int) this.red * 255;
        int green = (int) this.green * 255;
        int blue = (int) this.blue * 255;
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}