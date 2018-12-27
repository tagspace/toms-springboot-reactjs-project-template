package com.mywebsite.repository.util;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;

public class JdbcTemplateQuery {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String sql;
    private final HashMap<String, Object> map = new HashMap<>();

    public JdbcTemplateQuery(NamedParameterJdbcTemplate jdbcTemplate, String sqlTemplate) {
        this.sql = sqlTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplateQuery put(String key, Object value) {
        if(map.put(key, value) != null) {
            throw new RuntimeException("key already assigned: "+key);
        }
        return this;//for chaining
    }

    private String debug() {
        return map.toString();
    }

    public static JdbcTemplateQuery build(NamedParameterJdbcTemplate jdbcTemplate, String sqlTemplate) {
        return new JdbcTemplateQuery(jdbcTemplate, sqlTemplate);
    }

    public void updateOneRow() {
        int rowsUpdated = updateRows();
        if (rowsUpdated != 1) {
            throw new RuntimeException(String.format("Unexpected number of rows updated: %s [%s]", rowsUpdated, debug()));
        }
    }

    /**
     * @return the object
     * @throws EmptyResultDataAccessException if no result found
     */
    public <T> T loadOneObject(RowMapper<T> rowMapper)
            throws EmptyResultDataAccessException {
        try {
            return this.jdbcTemplate.queryForObject(this.sql, this.map, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(String.format("Unexpected results size: %s [%s]", 0, debug()));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException(String.format("Unexpected results size: %s [%s]", e.getActualSize(), debug()));
        }
    }

    public <T> T loadOneOrZeroObjects(RowMapper<T> rowMapper) {
        List<T> results = loadObjects(rowMapper);
        if (results.size() == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new RuntimeException(String.format("Unexpected results size: %s [%s]", results.size(), debug()));
        }
        return results.get(0);
    }

    public <T> List<T> loadObjects(RowMapper<T> rowMapper) {
        List<T> results = this.jdbcTemplate.query(this.sql, this.map, rowMapper);
        return results;
    }

    public boolean updateOneOrZeroRows() {
        int rowsUpdated = updateRows();
        if (rowsUpdated > 1) {
            throw new RuntimeException(String.format("Unexpected number of rows updated: %s [%s]", rowsUpdated, debug()));
        }
        return rowsUpdated == 1;
    }

    public int updateRows() {
        return this.jdbcTemplate.update(this.sql, this.map);
    }
}