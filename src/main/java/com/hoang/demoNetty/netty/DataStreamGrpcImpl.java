package com.hoang.demoNetty.netty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.Empty;
import com.hoang.plugins.NettyDemo;
import com.hoang.plugins.StreamDataServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@GRpcService
@Slf4j
public class DataStreamGrpcImpl extends StreamDataServiceGrpc.StreamDataServiceImplBase {

    @Autowired
    WebSocketServer webSocketServer;

    private final Gson gson = new GsonBuilder().create();

    @Override
    public StreamObserver<NettyDemo.IndexDataStream> streamData(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<NettyDemo.IndexDataStream>() {
            @Override
            public void onNext(NettyDemo.IndexDataStream value) {
                value.getDataStreamList().forEach(item -> {
//                    log.info("data ne {}", item);
                    webSocketServer.addQueue(item.getData());
                });
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}
