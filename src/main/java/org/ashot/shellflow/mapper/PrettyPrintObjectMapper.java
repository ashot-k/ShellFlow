package org.ashot.shellflow.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PrettyPrintObjectMapper extends ObjectMapper {
    public PrettyPrintObjectMapper() {
        this.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
