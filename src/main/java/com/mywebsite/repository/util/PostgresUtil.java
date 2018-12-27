package com.mywebsite.repository.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class PostgresUtil {

    public static PGobject jsonb(ObjectMapper mapper, Object obj) {
        try {
            String jsonString = mapper.writeValueAsString(obj);
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(jsonString);
            return pgObject;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
