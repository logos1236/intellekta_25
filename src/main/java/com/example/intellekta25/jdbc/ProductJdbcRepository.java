package com.example.intellekta25.jdbc;

import com.example.intellekta25.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> findAll() {
        String query = "SELECT * FROM product;";

        List<Product> result = jdbcTemplate.query(query, new ProductMapper());

        return result;
    }

    public Optional<Product> findById(long id) {
        String sql = "SELECT * FROM product WHERE product.id = ? LIMIT 1;";
        Product result = null;
        try {
            result = jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProductMapper());
        } catch (EmptyResultDataAccessException e) {

        }

        return Optional.ofNullable(result);
    }

    public Product saveAndFlush(Product product) {
        long changedId;

        if (product.getName() == null || product.getName().equals("")) {
            throw new IllegalArgumentException("Product name cant' be empty");
        }

        if (product.getId() > 0) {
            if (findById(product.getId()).isPresent()) {
                changedId = update(product);
            } else {
                changedId = insert(product);
            }
        } else {
            changedId = insert(product);
        }

        return findById(changedId).get();
    }

    private long update(Product product) {
        NamedParameterJdbcTemplate jdbcTemplateNamed = new NamedParameterJdbcTemplate(jdbcTemplate);
        String query = "UPDATE product SET name = :name WHERE id = :id;";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", product.getId());
        paramSource.addValue("name", product.getName());

        jdbcTemplateNamed.update(query, paramSource);

        return product.getId();
    }

    private long insert(Product product) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("product")
                .usingColumns("name")
                .usingGeneratedKeyColumns("id");

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", product.getId());
        paramSource.addValue("name", product.getName());

        return jdbcInsert.executeAndReturnKey(paramSource).longValue();
    }

    public static final class ProductMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet row, int i) throws SQLException {
            Product product = new Product();
            product.setId(row.getLong("id"));
            product.setName(row.getString("name"));

            return product;
        }
    }
}
