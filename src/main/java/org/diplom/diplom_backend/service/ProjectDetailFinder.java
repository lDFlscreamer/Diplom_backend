package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.DockerCommandConstant;
import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.service.Dao.ProjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to get information about launched project (image)
 */
@Service
public class ProjectDetailFinder {
    private static final Logger logger = LoggerFactory.getLogger(ProjectDetailFinder .class);

    @Autowired
    Terminal terminal;
    @Autowired
    Converter converter;
    @Autowired
    ProjectDAO projectDao;

    /**
     * Get information about open port
     *
     * @param imageName identifier of lauched project
     * @return instance of {@link Map} contains port data
     * key is exposed port
     * value is open port
     */
    public Map<Integer, Integer> getPortData(String imageName) {
        Process process;
        try {
            process = terminal.runCommand(MessageFormat.format(DockerCommandConstant.GET_CONTAINER_ID_BY_IMAGENAME, imageName));
            String containerID = String.join(GeneralConstants.SPACE, terminal.getOutputFromProcess(process));
            process = terminal.runCommand(MessageFormat.format(DockerCommandConstant.GET_PORT_INFORMATION_BY_IMAGE_NAME, containerID));
        } catch (IOException e) {
            logger.warn(String.format("can`t get port information about launched %s ",imageName),e);
            return null;
        }
        List<String> result = terminal.getOutputFromProcess(process);
        Map<Integer, Integer> portMap = new HashMap<>();

        String portPattern = ":(\\d{1,6})";
        String localPortPattern = "(\\d{1,6})/";


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
                    continue;
                }
                portMap.put(Integer.parseInt(localPort), Integer.parseInt(port));
            }
        }
        return portMap;

    }
}
