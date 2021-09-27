package com.hoang.demoNetty.netty;

import com.hoang.demoNetty.dto.WsMessage;
import com.hoang.demoNetty.entity.StockRealTimeEntity;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public interface WebSocketServer {
    void broadcast(WsMessage message);
    ChannelGroup getChannelGroup();
    Map<String, ChannelGroup> getChannelGroupMap();
    void addQueue(String message);
}
