package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity,Long> {
    Optional<ItemEntity> findByName(String name);
    ItemEntity findById(long id);

    @Query("""
        SELECT i FROM ItemEntity i
        WHERE (:query IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:category IS NULL OR i.category = :category)
        AND (:minPrice IS NULL OR i.price >= :minPrice)
        AND (:maxPrice IS NULL OR i.price <= :maxPrice)
""")
    Page<ItemEntity> searchItems(
            @Param("query") String query,
            @Param("category") Category category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
