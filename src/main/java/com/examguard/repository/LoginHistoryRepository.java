package com.examguard.repository;

import com.examguard.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findTop50ByUserIdOrderByLoginTimestampDesc(Long userId);
    List<LoginHistory> findTop100ByOrderByLoginTimestampDesc();
}

