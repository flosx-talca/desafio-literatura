package com.aluracursos.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            //System.out.println(objectMapper.readValue(json,clase));
            return objectMapper.readValue(json,clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}