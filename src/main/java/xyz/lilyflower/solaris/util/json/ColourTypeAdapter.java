package xyz.lilyflower.solaris.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import xyz.lilyflower.solaris.util.data.Colour;

public class ColourTypeAdapter extends TypeAdapter<Colour> {
    @Override
    public Colour read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String hex = in.nextString();
        return Colour.fromHex(hex);
    }

    @Override
    public void write(JsonWriter out, Colour value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toHex());
    }
}
