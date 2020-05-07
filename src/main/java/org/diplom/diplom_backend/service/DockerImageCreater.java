package org.diplom.diplom_backend.service;


import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
public class DockerImageCreater {
    @Autowired
    Terminal terminal;

    public List<String> createImage(String name, String dockerfilePath, String projectPath) {
        Process process = null;

        String terminalCommand = MessageFormat.format(DockerCommandConstant.CREATE_IMAGE, name, dockerfilePath, projectPath);
        try {
            process = terminal.runCommand(terminalCommand);
            terminal.runCommand(DockerCommandConstant.REMOVE_EXTRA_CONTAINER);
            String extraImages = terminal.getOutputFromProcess(terminal.runCommand("docker images --filter dangling=true -q --no-trunc")).stream().reduce((s, s2) -> s.concat(" ").concat(s2)).orElse("");
            if (!extraImages.equals("")) {
                terminal.runCommand(MessageFormat.format(DockerCommandConstant.REMOVE_EXTRA_IMAGE, extraImages));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 5/7/20 exception
            return null;
        }
        return terminal.getOutputFromProcess(process);
    }

    public List<String> getImages() {
        Process process = null;
        try {
            process = terminal.runCommand(DockerCommandConstant.IMAGES);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 5/7/20 exception
            return null;
        }
        return terminal.getOutputFromProcess(process);
    }

    public List<String> runImage(String imageNameOrId, String initialCommand) {
        Process process = null;
        String terminalCommand = MessageFormat.format(DockerCommandConstant.RUN_IMAGE, imageNameOrId, initialCommand == null ? "" : initialCommand);

        try {
            process = terminal.runCommand(terminalCommand);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 5/7/20 exception
            return null;
        }
        return terminal.getOutputFromProcess(process);

    }
}
