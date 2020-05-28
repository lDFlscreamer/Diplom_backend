package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.BuildStage;
import org.diplom.diplom_backend.entity.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.stream.Collectors;

@Service
public class DockerfileBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuilder.class);
    @Autowired
    private PathConstant pathConstant;

    public void createDockerfile(String filename, Project project,String login) throws IOException {
        StringBuilder dockerFileContent = createDockerfileContent(project,login);
        writeTofile(filename, dockerFileContent);
    }

    public StringBuilder createDockerfileContent(Project project,String login) {
        //todo:add user part
        StringBuilder dockerFileContent = new StringBuilder();
        boolean isInitialized = false;
        boolean hasExposedPort = false;
        String previousImageId = null;
        int imageCounter = 0;
        int i = 0;

        for (BuildStage stage : project.getBuildStages()) {
            String line;
            if (!stage.getImage().getId().equals(previousImageId)) {
                line = MessageFormat.format("FROM {0}:{1}", stage.getImage().getName(), stage.getVersion() != null ? stage.getVersion() : "latest");
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE)
                        .append(MessageFormat.format("WORKDIR  /usr/src/{0}",project.getName())).append(GeneralConstants.NEWLINE);
                if (previousImageId != null) {
                    line = MessageFormat.format("COPY --from={1} /usr/src/{0} .", project.getName(), imageCounter);
                    dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                }
            }
            if (!isInitialized) {
                line = MessageFormat.format("COPY ./{1}{0} . ", project.getName(), pathConstant.getProjectFolderName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                isInitialized = true;
            }

            //integrate commands
            int j = 0;
            for (String command : stage.getCommand()) {
                line = "RUN {0}";
                line = MessageFormat.format(line, command.replace("${mainClass}", project.getLaunchFilePath())).replace("${projectName}",project.getName());
                if ((i == (project.getBuildStages().size() - 1)) && j == (stage.getCommand().size() - 1)) {
                    if(project.getPorts()!=null && !project.getPorts().isEmpty()){
                        for (Integer port :
                                project.getPorts()) {
                            dockerFileContent.append(MessageFormat.format("EXPOSE {0}", port.toString())).append(GeneralConstants.NEWLINE);
                        }
                        hasExposedPort=true;
                    }
                    line = line.replace("RUN", hasExposedPort?"ENTRYPOINT":"CMD");
                }
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                j++;
            }
            previousImageId = stage.getImage().getId();
            i++;
        }
        logger.debug(MessageFormat.format("created dockerfile for project where name is {0}  and user where login is {1}",project.getName(),login));
        return dockerFileContent;
    }

    public void writeTofile(String filename, StringBuilder dockerfileContent) throws IOException {

        String path = pathConstant.getPath().concat(pathConstant.getDockerfileFolderName()).concat(filename);
        File dockerFile = new File(path);
        if (dockerFile.exists()) {

            try (FileReader in = new FileReader(dockerFile); BufferedReader reader = new BufferedReader(in)) {
                String contentInFile = reader.lines().collect(Collectors.joining(GeneralConstants.NEWLINE));
                if (contentInFile.equals(dockerfileContent.toString())) {
                    return ;
                }
            } catch (IOException e) {
                logger.warn(MessageFormat.format("cant read file  {0}",path),e);
            }
        }
        try {
            dockerFile.createNewFile();
        } catch (IOException e) {
            logger.warn(MessageFormat.format("cant create file  {0}",path),e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dockerFile))) {
            writer.write(dockerfileContent.toString());
        } catch (IOException e) {
            logger.warn(MessageFormat.format("cant write to file  {0}",path),e);
            throw e;
        }
    }
}
