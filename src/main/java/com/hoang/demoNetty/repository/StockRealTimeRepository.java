package com.hoang.demoNetty.repository;

import com.hoang.demoNetty.entity.StockRealTimeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface StockRealTimeRepository extends CrudRepository<StockRealTimeEntity, String> {

    @Query("SELECT item FROM StockRealtime item")
    List<StockRealTimeEntity> findRandom(Pageable pageable);
}
