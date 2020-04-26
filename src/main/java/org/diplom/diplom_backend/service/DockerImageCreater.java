package org.diplom.diplom_backend.service;


import org.diplom.diplom_backend.constant.CommandConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class DockerImageCreater {
    @Autowired
    Terminal terminal;

    public List<String> createImage(String name, String dockerfilePath, String projectPath) {

            String terminalCommand= MessageFormat.format(CommandConstant.CREATE_IMAGE, name, dockerfilePath, projectPath);


        return terminal.runCommandAndGetResult(terminalCommand);
    }

    public String getImages() {
        return terminal.runCommandAndGetResult(CommandConstant.IMAGES).toString();
    }

    public String runImage(String imageNameOrId,String initialCommand){
        String terminalCommand= MessageFormat.format(CommandConstant.RUN_IMAGE, imageNameOrId,initialCommand);
        return terminal.runCommandAndGetResult(terminalCommand).toString();

    }
}
