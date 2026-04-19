package lk.ghanim.api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor

public class ProductSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findSimilarProductIds(
            float[] queryEmbedding, int limit) {

        String vectorStr = IntStream.range(0, queryEmbedding.length)
                .mapToObj(i -> String.valueOf(queryEmbedding[i]))
                .collect(Collectors.joining(",", "[", "]"));

        String sql = """
            SELECT id FROM products
            WHERE embedding IS NOT NULL
            AND active = true
            ORDER BY embedding <=> ?::vector
            LIMIT ?
            """;

        return jdbcTemplate.queryForList(
                sql,
                Long.class,
                vectorStr,
                limit
        );
    }

    public void updateProductEmbedding(Long productId, float[] embedding) {
        String vectorStr = IntStream.range(0, embedding.length)
                .mapToObj(i -> String.valueOf(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));

        jdbcTemplate.update(
                "UPDATE products SET embedding = ?::vector WHERE id = ?",
                vectorStr,
                productId
        );
    }
}