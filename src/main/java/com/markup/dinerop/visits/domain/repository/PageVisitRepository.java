package com.markup.dinerop.visits.domain.repository;

import com.markup.dinerop.visits.domain.entity.PageVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
}
