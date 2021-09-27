package com.hoang.demoNetty.grpc;

import com.hoang.plugins.NettyDemo;

public interface ClientGrpcStream {
    void pushData(NettyDemo.IndexDataStream indexDataStream);
}
