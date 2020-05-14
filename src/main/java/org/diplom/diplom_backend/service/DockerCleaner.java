package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.Application;
import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;

@Service
public class DockerCleaner {
    private static final Logger logger = LoggerFactory.getLogger(DockerCleaner.class);
    @Autowired
    private Terminal terminal;


    @Scheduled(cron ="${clean.image.cron.expression}")
    public void imageClean(){
       logger.info("Docker clean extra image");
        try {
            String extraImages = terminal.getOutputFromProcess(terminal.runCommand(DockerCommandConstant.GET_LIST_OF_EXTRA_IMAGE)).stream().reduce((s, s2) -> s.concat(" ").concat(s2)).orElse("");
            if (!extraImages.equals("")) {
                terminal.runCommand(MessageFormat.format(DockerCommandConstant.REMOVE_IMAGE, extraImages));
            }
        }catch (IOException e){
            logger.warn("can`t remove extra images",e);
        }
    }

    @Scheduled(cron ="${clean.container.cron.expression}")
    public void containerClean(){
        logger.info("Docker clean extra containers");
        try {
            terminal.runCommand(DockerCommandConstant.REMOVE_EXTRA_CONTAINER);
        }catch (IOException e){
            logger.warn("can`t remove extra containers",e);
        }
    }
}
