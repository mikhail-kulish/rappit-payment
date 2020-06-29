package io.rappit.services.payment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingDeserializer<T> extends JsonDeserializer<T> {
    private final static Logger log = LoggerFactory.getLogger(LoggingDeserializer.class);
    private final JsonDeserializer<T> origin;

    public LoggingDeserializer(JsonDeserializer<T> origin) {
        this.origin = origin;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return origin.deserialize(p, ctxt);
    }
}
