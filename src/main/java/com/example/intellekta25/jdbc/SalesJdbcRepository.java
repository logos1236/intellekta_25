package com.example.intellekta25.jdbc;

import com.example.intellekta25.entity.Product;
import com.example.intellekta25.entity.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class SalesJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductJdbcRepository productJdbcRepository;

    @Autowired
    public SalesJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int count() {
        String sql = "SELECT COUNT(sales.id) FROM sales INNER JOIN product ON sales.product = product.id;";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Optional<Sales> findById(long id) {
        String sql = "SELECT sales.*, product.id as product_id, product.name as product_name FROM sales INNER JOIN product ON sales.product = product.id WHERE sales.id = ? LIMIT 1;";
        Sales result = jdbcTemplate.queryForObject(sql, new Object[]{id}, new SalesRowMapper());

        return Optional.of(result);
    }

    public List<Sales> findAll() {
        String query = "SELECT sales.*, product.id as product_id, product.name as product_name FROM sales INNER JOIN product ON sales.product = product.id;";
        List<Sales> result = jdbcTemplate.query(query, new SalesRowMapper());

        return result;
    }

    public Sales saveAndFlush(Sales sales) {
        long changedId;

        if (sales.getProduct() == null
                || sales.getPrice() <= 0
                || sales.getDateSale() == null
                || sales.getDateGoodIncoming() == null) {
            throw new IllegalArgumentException("Product name cant' be empty");
        }

        productJdbcRepository.saveAndFlush(sales.getProduct());

        if (sales.getId() > 0) {
            if (findById(sales.getId()).isPresent()) {
                changedId = update(sales);
            } else {
                changedId = insert(sales);
            }
        } else {
            changedId = insert(sales);
        }

        return findById(changedId).get();
    }

    private long update(Sales sales) {
        NamedParameterJdbcTemplate jdbcTemplateNamed = new NamedParameterJdbcTemplate(jdbcTemplate);
        String query = "UPDATE sales SET price = :price, date_good_incoming = :date_good_incoming, date_sale := date_sale, product = :product WHERE id = :id;";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", sales.getId());
        paramSource.addValue("price", sales.getPrice());
        paramSource.addValue("date_good_incoming", sales.getDateGoodIncoming());
        paramSource.addValue("date_sale", sales.getDateSale());
        paramSource.addValue("product", sales.getProduct().getId());

        jdbcTemplateNamed.update(query, paramSource);

        return sales.getId();
    }

    private long insert(Sales sales) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("sales")
                .usingColumns("price", "date_good_incoming", "date_sale", "product")
                .usingGeneratedKeyColumns("id");

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", sales.getId());
        paramSource.addValue("price", sales.getPrice());
        paramSource.addValue("date_good_incoming", sales.getDateGoodIncoming());
        paramSource.addValue("date_sale", sales.getDateSale());
        paramSource.addValue("product", sales.getProduct().getId());

        return jdbcInsert.executeAndReturnKey(paramSource).longValue();
    }

    public static final class SalesRowMapper implements RowMapper<Sales> {
        @Override
        public Sales mapRow(ResultSet row, int rowNum) throws SQLException {
            Sales sales = new Sales();
            sales.setId((row.getLong("id")));
            sales.setPrice(row.getLong("price"));
            sales.setDateGoodIncoming(row.getDate("date_good_incoming"));
            sales.setDateSale(row.getDate("date_sale"));

            Product product = new Product();
            product.setId(row.getLong("product_id"));
            product.setName(row.getString("product_name"));
            sales.setProduct(product);

            return sales;
        }
    }
}
