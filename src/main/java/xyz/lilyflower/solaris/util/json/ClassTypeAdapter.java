package xyz.lilyflower.solaris.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class<?>> {
    @Override
    public void write(JsonWriter out, Class<?> value) throws IOException {
        out.value(value == null ? null : value.getName());
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            return Class.forName(in.nextString());
        } catch (ClassNotFoundException exception) {
            throw new IOException(exception);
        }
    }
}
