package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.diplom.diplom_backend.constant.GeneralConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Contains two method for clean system .
 *
 * @author Tverdokhlib
 */
@Service
public class DockerCleaner {
    private static final Logger logger = LoggerFactory.getLogger(DockerCleaner.class);
    @Autowired
    private Terminal terminal;

    /**
     * this is scheduled method. clean extra images which was produced where user launch project
     */
    @Scheduled(cron = "${clean.image.cron.expression}")
    public void imageClean() {
        logger.info("Docker clean extra image");
        try {
            String extraImages = terminal.getOutputFromProcess(terminal.runCommand(DockerCommandConstant.GET_LIST_OF_EXTRA_IMAGE)).stream().reduce((s, s2) -> s.concat(" ").concat(s2)).orElse(GeneralConstants.EMPTY);
            if (!extraImages.equals(GeneralConstants.EMPTY)) {
                terminal.runCommand(MessageFormat.format(DockerCommandConstant.REMOVE_IMAGE, extraImages));
            }
        } catch (IOException e) {
            logger.warn("can`t remove extra images", e);
        }
    }

    /**
     * this is scheduled method. clean extra container which was produced where user launch project
     * For each step  in dockerfile generates container and this method remove container that was not used anymore
     */
    @Scheduled(cron = "${clean.container.cron.expression}")
    public void containerClean() {
        logger.info("Docker clean extra containers");
        try {
            terminal.runCommand(DockerCommandConstant.REMOVE_EXTRA_CONTAINER);
        } catch (IOException e) {
            logger.warn("can`t remove extra containers", e);
        }
    }

    public void removeImages(String userlogin) {
        try {
            String command = MessageFormat.format(DockerCommandConstant.GET_LIST_OF_IMAGE_BY_USER_LOGIN, userlogin);
            List<String> userImages = terminal.getOutputFromProcess(terminal.runCommand(command));
            if (userImages.isEmpty()) return;
            for (String image :
                    userImages) {
                Process process = terminal.runCommand(MessageFormat.format(DockerCommandConstant.GET_CONTAINER_ID_BY_IMAGENAME, image));
                String containerID = String.join(GeneralConstants.SPACE, terminal.getOutputFromProcess(process));
                if (!containerID.isEmpty()){
                    terminal.runCommand(MessageFormat.format(DockerCommandConstant.STOP_CONTAINER, containerID));
                }
                terminal.runCommand(MessageFormat.format(DockerCommandConstant.REMOVE_IMAGE, image));
            }
        } catch (IOException e) {
            logger.warn("can`t remove images", e);
        }
    }

}
