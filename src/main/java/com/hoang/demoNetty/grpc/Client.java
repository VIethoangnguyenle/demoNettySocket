package com.hoang.demoNetty.grpc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoang.demoNetty.entity.StockRealTimeEntity;
import com.hoang.demoNetty.repository.StockRealTimeRepository;
import com.hoang.plugins.NettyDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
@EnableScheduling
public class Client {
    private final SecureRandom random = new SecureRandom();

    private final Gson gson = new GsonBuilder().create();

    @Autowired
    StockRealTimeRepository stockRealTimeRepository;

    @Autowired
    ClientGrpcStream clientGrpcStream;

    @Scheduled(fixedRate = 30, initialDelayString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60) }")
    private void stream() {
        long count = stockRealTimeRepository.count();
        StockRealTimeEntity data = stockRealTimeRepository.findRandom(PageRequest.of(random.nextInt((int) count), 1)).get(0);
        NettyDemo.DataStream dataStream = NettyDemo.DataStream.newBuilder()
                        .setName(data.getS())
                                .setData(gson.toJson(data))
                                        .build();
        clientGrpcStream.pushData(NettyDemo.IndexDataStream.newBuilder().addDataStream(dataStream).build());
    }
}
