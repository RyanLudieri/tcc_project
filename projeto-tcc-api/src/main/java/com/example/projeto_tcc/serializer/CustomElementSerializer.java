package com.example.projeto_tcc.serializer;

import com.example.projeto_tcc.entity.AbstractElement;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class CustomElementSerializer extends JsonSerializer<List<? extends AbstractElement>> {

    @Override
    public void serialize(List<? extends AbstractElement> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (AbstractElement element : value) {
            gen.writeNumber(element.getIndex());  // <-- só o index
        }
        gen.writeEndArray();
    }
}

