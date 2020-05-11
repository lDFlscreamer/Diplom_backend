package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.service.Dao.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProjectDetailFinder {
    @Autowired
    Terminal terminal;
    @Autowired
    Converter converter;
    @Autowired
    ProjectDao projectDao;

    public Map<Integer, Integer> getPortData(String projectId, String userLogin) {

        Project project = projectDao.getProjectById(projectId);
        String imageName = converter.getImageName(project, userLogin);
        Process process = null;
        try {
            process = terminal.runCommand(MessageFormat.format(DockerCommandConstant.GET_CONTAINER_ID_BY_IMAGENAME, imageName));
            String containerID = String.join(GeneralConstants.SPACE, terminal.getOutputFromProcess(process));
            process = terminal.runCommand(MessageFormat.format(DockerCommandConstant.GET_PORT_INFORMATION_BY_IMAGE_NAME, containerID));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 5/7/20 exception
            return null;
        }
        List<String> result = terminal.getOutputFromProcess(process);
        Map<Integer, Integer> portMap = new HashMap<>();

        String portPattern = ":(\\d{1,6})";
        String localPortPattern = "(\\d{1,6})/";

        // Create a Pattern object
        Pattern r = Pattern.compile(portPattern);
        Pattern r1 = Pattern.compile(localPortPattern);
        for (String line :
                result) {
            Matcher m = r.matcher(line);
            Matcher m2 = r1.matcher(line);
            if (m.find() && m2.find()) {
                String localPort;
                String port;
                try {
                    port = m.group(1);
                    localPort = m2.group(1);
                } catch (RuntimeException e) {
                    // TODO: 5/11/20 logger
                    continue;
                }
                portMap.put(Integer.parseInt(localPort), Integer.parseInt(port));
            }
        }
        return portMap;

    }
}
