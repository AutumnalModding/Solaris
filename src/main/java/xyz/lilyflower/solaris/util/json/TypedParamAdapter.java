package xyz.lilyflower.solaris.util.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import xyz.lilyflower.solaris.util.data.TypedParam;

public class TypedParamAdapter extends TypeAdapter<TypedParam> {
    @Override
    public void write(JsonWriter out, TypedParam value) throws IOException {
        out.beginObject();
        out.name("type").value(value.type());
        out.name("value");
        writeValue(out, value.value(), value.type());
        out.endObject();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public TypedParam read(JsonReader in) throws IOException {
        String type = null;
        Object value = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("type")) {
                type = in.nextString();
            } else if (name.equals("value")) {
                value = readValue(in, type);
            }
        }
        in.endObject();

        return new TypedParam(type, value);
    }

    private Object readValue(JsonReader in, String type) throws IOException {
        return switch(type) {
            case "int" -> in.nextInt();
            case "double" -> in.nextDouble();
            case "float" -> (float) in.nextDouble();
            case "long" -> in.nextLong();
            case "boolean" -> in.nextBoolean();
            case "string" -> in.nextString();
            default -> throw new IOException("Unknown type: " + type);
        };
    }

    private void writeValue(JsonWriter out, Object value, String type) throws IOException {
        switch(type) {
            case "int", "long", "double", "float" -> out.value((Number) value);
            case "boolean" -> out.value((Boolean) value);
            case "string" -> out.value((String) value);
            default -> throw new IOException("Unknown type: " + type);
        }
    }
}