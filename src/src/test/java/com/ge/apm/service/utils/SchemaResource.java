package com.ge.apm.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.domain.pure.TableSchema;

import java.io.IOException;
import java.net.URL;

public class SchemaResource {

    protected TableSchema getSchema(String name) {
        TableSchema schema;
        URL path = this.getClass().getClassLoader().getResource(name);
        // read json file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            schema = objectMapper.readValue(path, TableSchema.class);
        } catch (IOException e) {
            schema = null;
        }
        return schema;
    }
}
