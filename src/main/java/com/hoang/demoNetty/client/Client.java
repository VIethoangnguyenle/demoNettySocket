package com.hoang.demoNetty.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoang.demoNetty.dto.WsMessage;
import com.hoang.demoNetty.entity.StockRealTimeEntity;
import com.hoang.demoNetty.netty.NettyServiceListener;
import com.hoang.demoNetty.netty.WebSocketServer;
import com.hoang.demoNetty.repository.StockRealTimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.security.SecureRandom;

@Service
@Slf4j
@EnableScheduling
public class Client {

    private final Gson gson = new GsonBuilder().create();

    @Autowired
    WebSocketServer webSocketServer;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    StockRealTimeRepository stockRealTimeRepository;

    @Scheduled(fixedRate = 30, initialDelayString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60) }")
    private void stream() {
        long count = stockRealTimeRepository.count();
        StockRealTimeEntity data = stockRealTimeRepository.findRandom(PageRequest.of(random.nextInt((int) count), 1)).get(0);
        webSocketServer.stackData(data);
    }

    @Scheduled(fixedRate = 30, initialDelayString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60) }")
    private void readData() {
//        log.info("Data {}", webSocketServer.getData());
    }
}
