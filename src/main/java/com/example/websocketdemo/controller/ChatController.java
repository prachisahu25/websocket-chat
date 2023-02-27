package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    public final static String TOPIC_URL = "/topic/public/";
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * This method is used to send the typing notification all the user logged in
     *
     * @param chatMessage
     * @return
     */
    @MessageMapping("/chat.type")
    public ChatMessage sendTypeStatus(@Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend(TOPIC_URL + chatMessage.getRoom(), chatMessage);
        return chatMessage;
    }

    /**
     * This method is used to send the message to the room logged in
     *
     * @param chatMessage
     * @return
     */

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        logger.info("Message sent by " + chatMessage.getSender() + " from  room " + chatMessage.getRoom() + " : message  " + chatMessage.getContent());
        simpMessagingTemplate.convertAndSend(TOPIC_URL + chatMessage.getRoom(), chatMessage);
        return chatMessage;
    }

    /**
     * This method is used to add the users to the room.
     *
     * @param chatMessage
     * @param headerAccessor
     * @return
     */

    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        chatMessage.getUserList().add(chatMessage.getSender());
        simpMessagingTemplate.convertAndSend(TOPIC_URL + chatMessage.getRoom(), chatMessage);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("room", chatMessage.getRoom());


        return chatMessage;
    }

}
