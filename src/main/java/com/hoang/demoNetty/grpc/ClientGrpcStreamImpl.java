package com.hoang.demoNetty.grpc;

import com.google.protobuf.Empty;
import com.hoang.plugins.NettyDemo;
import com.hoang.plugins.StreamDataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ClientGrpcStreamImpl implements ClientGrpcStream{
    private StreamDataServiceGrpc.StreamDataServiceStub serviceStub;
    protected ManagedChannel channel;

    @Value("${grpc.stream.host}")
    String host;

    @Value("${grpc.stream.port}")
    Integer port;

    @Override
    public void pushData(NettyDemo.IndexDataStream indexDataStream) {
        getAsync().streamData(new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        }).onNext(indexDataStream);
    }

    StreamDataServiceGrpc.StreamDataServiceStub getAsync() {
        if (serviceStub == null) {
            serviceStub =  StreamDataServiceGrpc.newStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().keepAliveWithoutCalls(true).build());
        }
        return serviceStub;
    }

    protected void releaseChannel() throws InterruptedException {
        if (channel != null) {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
