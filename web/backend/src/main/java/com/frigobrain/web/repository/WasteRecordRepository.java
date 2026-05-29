package com.frigobrain.web.repository;

import com.frigobrain.web.entity.WasteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WasteRecordRepository extends JpaRepository<WasteRecord, Long> {

    List<WasteRecord> findByWasteDateBetweenOrderByWasteDateAsc(LocalDate start, LocalDate end);

    @Query("SELECT w.categoryName, SUM(w.estimatedCost), COUNT(w) " +
           "FROM WasteRecord w " +
           "WHERE w.wasteDate BETWEEN :start AND :end " +
           "GROUP BY w.categoryName")
    List<Object[]> findWasteByCategoryInDateRange(@Param("start") LocalDate start,
                                                   @Param("end") LocalDate end);

    @Query("SELECT SUM(w.estimatedCost), COUNT(w) " +
           "FROM WasteRecord w " +
           "WHERE w.wasteDate BETWEEN :start AND :end")
    Object[] findWasteSummaryInDateRange(@Param("start") LocalDate start,
                                          @Param("end") LocalDate end);
}
