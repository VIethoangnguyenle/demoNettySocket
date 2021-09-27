package com.hoang.demoNetty.multithread;

import com.hoang.demoNetty.dto.WsMessage;
import com.hoang.demoNetty.netty.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakeDataThread implements Runnable{

    @Autowired
    WebSocketServer webSocketServer;

    @Override
    public void run() {
        log.info("Fake data threading");
        webSocketServer.broadcast(WsMessage.buildData(webSocketServer.getData()));
    }
}
