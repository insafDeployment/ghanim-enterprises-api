package lk.ghanim.api.repository;

import lk.ghanim.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategorySlug(String slug);
    List<Product> findByActiveTrue();
    List<Product> findByCategorySlugAndActiveTrue(String slug);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
