package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.model.RentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("""
        SELECT COUNT(*) from RentEntity r
        WHERE (r.requester.id = :requesterId)
            AND r.status IN ('PENDING', 'APPROVED', 'ONGOING')
""")
    int getCurrentRentsCount(@Param("requesterId")  Long requesterId);

    @Modifying
    @Query("""
        UPDATE RentEntity r
        SET r.status = 'REJECTED'
        WHERE r.item.id = :itemId
            AND r.id <> :rentId
            AND r.status = 'PENDING'
            AND r.startDate < :endDate AND r.endDate > :startDate
""")
    void rejectRentsWithConflictingDates(
            @Param("rentId") Long rentId,
            @Param("itemId") Long itemId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );

    @Query("""
        SELECT r from RentEntity r
            WHERE r.item.id = :itemId
            AND r.status = 'APPROVED'
""")
    List<RentEntity> getApprovedRents(@Param("itemId") Long itemId);


    @Query("""
        SELECT r.item
        FROM RentEntity r
        WHERE r.status IN (:statuses)
        GROUP BY r.item
        ORDER BY COUNT(r) DESC
""")
    Page<ItemEntity> getTop3RentedItems(@Param("statuses") List<RentStatus> statuses, Pageable pageable);

    @Query("""
    SELECT r FROM RentEntity r
    WHERE r.status IN ('PENDING', 'APPROVED', 'ONGOING')
""")
    List<RentEntity> findAllActiveRents();

}
