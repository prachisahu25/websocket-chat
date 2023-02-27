package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageTypeMessageCondition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat.type")
//    @SendTo("/topic/public")
    public ChatMessage sendTypeStatus(@Payload ChatMessage chatMessage) {

        simpMessagingTemplate.convertAndSend("/topic/public/"+ chatMessage.getRoom(), chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        simpMessagingTemplate.convertAndSend("/topic/public/" + chatMessage.getRoom(), chatMessage);
        return chatMessage;
    }


    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        chatMessage.getUserList().add(chatMessage.getSender());
        simpMessagingTemplate.convertAndSend("/topic/public/"+ chatMessage.getRoom(), chatMessage);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("room",chatMessage.getRoom());

        return chatMessage;
    }

}
