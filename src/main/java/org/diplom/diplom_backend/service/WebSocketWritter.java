package org.diplom.diplom_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketWritter {
    @Autowired
    private SimpMessagingTemplate template;


    public  void send(String imageName,String typeOfMessage,Object payload){
        String adr = "/".concat(imageName);
        if (!typeOfMessage.isEmpty()){
            adr=adr.concat("/").concat(typeOfMessage);
        }
        template.convertAndSend(adr, payload);
    }

    public void sendOutput(String imagename,Object payload){
        send(imagename,"",payload);
    }

    public void sendLogs(String imagename,Object payload){
        send(imagename,"logs",payload);
    }
}
