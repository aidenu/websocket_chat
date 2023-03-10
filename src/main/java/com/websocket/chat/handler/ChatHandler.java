package com.websocket.chat.handler;

import com.websocket.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;
    private static List<WebSocketSession> list = new ArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("[{}] message : {}", session.getId(), payload);
        chatService.saveData(payload);
        list.stream().forEach(sess -> extractSendMessage(sess, message));
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);
//        log.info("recent Chat history :: {}", chatService.getChatList());
        log.info("today Chat history :: {}", chatService.getTodayChatList());
        log.info("Connect Session [{}]", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Disconnect Session [{}]", session.getId());
        list.remove(session);
    }

    private void extractSendMessage(WebSocketSession session, TextMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
