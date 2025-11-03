package org.ashot.shellflow.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DefaultObjectMapper extends ObjectMapper {
    public DefaultObjectMapper() {
        this.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
