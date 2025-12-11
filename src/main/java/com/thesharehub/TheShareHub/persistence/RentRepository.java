package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.model.Rent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<RentEntity,Long> {

    @Query("""
        SELECT r FROM RentEntity r
        WHERE (r.rentier.id = :rentierId)
        ORDER BY r.id DESC
""")
    Page<RentEntity> getReceivedRequests(@Param("rentierId") Long userId, Pageable pageable);

    @Query("""
        SELECT r FROM RentEntity r
        WHERE (r.requester.id = :requesterId)
        ORDER BY r.id DESC
""")
    Page<RentEntity> getSentRequests(@Param("requesterId") Long userId, Pageable pageable);
}
