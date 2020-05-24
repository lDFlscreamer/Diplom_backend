package org.diplom.diplom_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketWritter {
    @Autowired
    private SimpMessagingTemplate template;


    public  void send(String imageName,String typeOfMessage,Object payload){
        template.convertAndSend("/".concat(imageName).concat("/").concat(typeOfMessage), payload);
    }

    public void sendOutput(String imagename,Object payload){
        send(imagename,"output",payload);
    }

    public void sendLogs(String imagename,Object payload){
        send(imagename,"logs",payload);
    }
}
