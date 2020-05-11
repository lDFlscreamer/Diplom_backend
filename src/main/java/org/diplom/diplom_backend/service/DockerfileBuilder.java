package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.BuildStage;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.stream.Collectors;

@Service
public class DockerfileBuilder {
    //todo:add logger
    @Autowired
    private ImageRepository imageRepository;

    public boolean createDockerfile(String filename, Project project) {
        //todo:filename - projectName@UserID
        StringBuilder dockerFileContent = createDockerfileContent(project);
        return writeTofile(filename, dockerFileContent);
    }

    public StringBuilder createDockerfileContent(Project project) {
        //todo:add user
        StringBuilder dockerFileContent = new StringBuilder();
        boolean isInitialized = false;
        boolean hasExposedPort = false;
        String previousImageId = null;
        int imageCounter = 0;
        int i = 0;

        for (BuildStage stage : project.getBuildStages()) {
            String line;
            if (!stage.getImage().getId().equals(previousImageId)) {
                line = MessageFormat.format("FROM {0}:{1}", stage.getImage().getImageName(), stage.getVersion() != null ? stage.getVersion() : "latest");
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE)
                        .append(MessageFormat.format("WORKDIR  /usr/src/{0}",project.getProjectName())).append(GeneralConstants.NEWLINE);
                if (previousImageId != null) {
                    line = MessageFormat.format("COPY --from={1} /usr/src/{0} .", project.getProjectName(), imageCounter);
                    dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                }
            }
            if (!isInitialized) {
                line = MessageFormat.format("COPY ./{1}{0} . ", project.getProjectName(), PathConstant.ProjectFolderName);
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                isInitialized = true;
            }

            //integrate commands
            int j = 0;
            for (String command : stage.getCommand()) {
                line = "RUN {0}";
                line = MessageFormat.format(line, command.replace("${mainClass}", project.getMainClass())).replace("${projectName}",project.getProjectName());
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

        return dockerFileContent;
    }

    public boolean writeTofile(String filename, StringBuilder dockerfileContent) {

        File myObj = new File(PathConstant.path.concat(PathConstant.DockerfileFolderName).concat(filename));
        if (myObj.exists()) {

            try (FileReader in = new FileReader(myObj); BufferedReader reader = new BufferedReader(in)) {
                String contentInFile = reader.lines().collect(Collectors.joining(GeneralConstants.NEWLINE));
                if (contentInFile.equals(dockerfileContent.toString())) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            myObj.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(myObj))) {
            writer.write(dockerfileContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }
}
