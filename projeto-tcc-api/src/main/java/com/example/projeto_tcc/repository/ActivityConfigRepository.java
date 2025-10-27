package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.ActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityConfigRepository extends JpaRepository<ActivityConfig, Long> {

    Optional<ActivityConfig> findByActivityId(Long activityId);

    ActivityConfig findByActivity(Activity activity);

    void deleteByActivityIdIn(List<Long> activityIds);


    @Query("SELECT ac FROM ActivityConfig ac WHERE ac.activity.index > :currentIndex ORDER BY ac.activity.index ASC")
    ActivityConfig findNextActivityConfig(@Param("currentIndex") Long currentIndex);

    List<ActivityConfig> findByDeliveryProcessId(Long deliveryProcessId);

    void deleteByDeliveryProcessId(Long deliveryProcessId);

}


