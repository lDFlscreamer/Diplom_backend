package org.diplom.diplom_backend.service;


import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.diplom.diplom_backend.constant.GeneralConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;


/**
 * class contains method for create and run docker images
 *
 * @author Tverdokhlib
 * @see ProjectLauncher
 */
@Service
public class DockerImageCreator {
    private static final Logger logger = LoggerFactory.getLogger(DockerImageCreator.class);
    @Autowired
    private Terminal terminal;
    @Autowired
    private WebSocketWriter webSocketWriter;


    /**
     * Create docker image
     *
     * @param name name of docker image. Use for run image
     * @param dockerfilePath filePath to file with docker instructions
     * @param path   path to directory
     * @return {@code true} if everything fine
     */
    public boolean createImage(String name, String dockerfilePath, String path){
        Process process;
        boolean isOk = true;

        String terminalCommand = MessageFormat.format(DockerCommandConstant.CREATE_IMAGE, name, dockerfilePath, path);
        try {
            process = terminal.runCommand(terminalCommand);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cant create image (comand is {0})", terminalCommand), e);
            return false;
        }
        List<String> outputFromProcess = terminal.getOutputFromProcess(process);
        List<String> errorFromProcess = terminal.getErrorFromProcess(process);
        if (!errorFromProcess.isEmpty()) {
            outputFromProcess.addAll(errorFromProcess);
            isOk = false;
        }
        if (!isOk)
            webSocketWriter.sendOutput(GeneralConstants.SLASH.concat(name), String.join(GeneralConstants.NEWLINE, outputFromProcess));
        webSocketWriter.sendLogs(GeneralConstants.SLASH.concat(name), String.join(GeneralConstants.NEWLINE, outputFromProcess));
        return isOk;
    }

    /**
     * Get list of images
     *
     * @return output of "docker images"
     */
    public List<String> getImages() {
        Process process;
        try {
            process = terminal.runCommand(DockerCommandConstant.IMAGES);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cant get list of  images (comand is {0})", DockerCommandConstant.IMAGES), e);
            return null;
        }
        return terminal.getOutputFromProcess(process);
    }

    /**
     * Run created docker image
     *
     * @param imageNameOrId identifier of image
     * @param initialCommand command to execute on this image
     * @return launched image
     *
     * @see Process
     */
    public Process runImage(String imageNameOrId, String initialCommand) {
        Process process;
        String terminalCommand = MessageFormat.format(DockerCommandConstant.RUN_IMAGE, imageNameOrId, initialCommand == null ? GeneralConstants.EMPTY : initialCommand);

        try {
            process = terminal.runCommand(terminalCommand);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cant run image (comand is {0})", terminalCommand), e);
            return null;
        }
        return process;
    }

    public Process removeImage(String imageNameOrId) {
        Process process;
        String terminalCommand = MessageFormat.format(DockerCommandConstant.REMOVE_IMAGE, imageNameOrId);
        try {
            process = terminal.runCommand(terminalCommand);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cant remove image (comand is {0})", terminalCommand), e);
            return null;
        }
        return process;
    }
}
