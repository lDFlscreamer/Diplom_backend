package org.diplom.diplom_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * service class contains method for output data into socket
 *
 * @author Tverdokhlib
 */
@Service
public class WebSocketWriter {
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * general method for send data
     *
     * @param imageName identifier of launched project ,uses for address of socket
     * @param typeOfMessage uses for
     * @param payload data which will be send to socket
     */
    public  void send(String imageName, String typeOfMessage, Object payload){
        String adr = "/".concat(imageName);
        if (!typeOfMessage.isEmpty()){
            adr=adr.concat("/").concat(typeOfMessage);
        }
        template.convertAndSend(adr, payload);
    }


    /***
     * send {@code payload}  to address {@code imageName/} via socket
     * @param imageName identifier of launched project
     * @param payload data for send
     */
    public void sendOutput(String imageName,Object payload){
        send(imageName,"",payload);
    }

    /**
     * send {@code payload}  to address {@code imageName/logs} via socket
     * @param imageName identifier of launched project
     * @param payload data for send
     */
    public void sendLogs(String imageName,Object payload){
        send(imageName,"logs",payload);
    }
}
