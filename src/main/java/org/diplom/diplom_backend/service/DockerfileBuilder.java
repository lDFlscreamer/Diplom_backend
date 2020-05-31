package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.SystemConstant;
import org.diplom.diplom_backend.entity.BuildStage;
import org.diplom.diplom_backend.entity.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for generate dockerfile. Create instructions  and write it into special file.
 * Create  Dockerfile in DockerfileDirectory ({@link SystemConstant})
 * Utility class for {@link ProjectLauncher}
 *
 * @author Tverdokhlib
 * @see ProjectLauncher
 */
@Service
public class DockerfileBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuilder.class);
    @Autowired
    private SystemConstant systemConstant;

    /**
     * Main method for convert {@link Project} {@link BuildStage} to Docker instructions.
     * Create Dockerfile with this instruction.
     * @param filename Name of docker file
     * @param project project instance. Which must be launched
     * @param login user login. we need to identify which user changes must apply to project
     * @throws IOException throw if something went wrong in process of creating  dockerfile
     *
     * @see Project
     * @see Converter
     */
    public void createDockerfile(String filename, Project project,String login) throws IOException {
        StringBuilder dockerFileContent = createDockerfileContent(project,login);
        writeTofile(filename, dockerFileContent);
    }


    /**
     * Convert  {@link Project} {@link BuildStage} to Docker instructions
     *
     *FOR each  stage  :<br>
     *___________IF not a same image  : <br>
     *________________use baseImage  :"FROM {imageName}:{Version}"<br>
     *________________set work directory: "WORKDIR /usr/src/{projectName}"<br>
     *________________IF it is not  first baseImage :<br>
     *________________    Copy project file from previous stage : "COPY --from={1} /usr/src/{projectName} ."<br>
     *________________ENIF<br>
     *___________ENDIF<br>
     *___________IF it first step :<br>
     *________________copy projectFile into image work directory: "COPY ./{projectFolderName}/{projectName} ."<br>
     *________________set link image to user via label :"LABEL user={login}"<br>
     *________________Copy script for applying user changes: "COPY ./{UtilsFolderName} ../"<br>
     *________________create Changes file for situation if user haven`t changes: "RUN  touch ../Changes "<br>
     *________________Copy user changes : "COPY ./{serResourcesFolderName}/{login}/{projectName} ../"<br>
     *________________apply changes : "RUN  ../{modifierScriptName}.sh ../Changes "<br>
     *___________ENDIF<br>
     * ________________FOR each command in stage :<br>
     * _____________________ replace special value in command "${mainClass}","${projectName}","${userLogin}"<br>
     * _____________________ IF it is not a last command in last stage:<br>
     * ___________________________add "Run {command}"<br>
     * _____________________ ENDIF<br>
     * _____________________ IF last command in last stage :<br>
     * _________________________ IF have port list:<br>
     * ______________________________FOR  each port :<br>
     * ___________________________________expose port :"EXPOSE {port number}"<br>
     * ______________________________ENDFOR<br>
     * _________________________ ENDIF<br>
     * _________________________ IF has exposed port<br>
     * ______________________________add "ENTRYPOINT command"<br>
     * _________________________ELSE<br>
     * ______________________________add "CMD command"<br>
     * _________________________ENDIF<br>
     * _____________________ENDIF<br>
     * ___________ENDFOR<br>
     *ENDFOR<br>
     *
     * @param project project instance. Which must be launched
     * @param login user login. we need to identify which user changes must apply to project
     * @return Dockerfile instruction in {@link StringBuilder} instance
     *
     * @see Project
     * @see org.diplom.diplom_backend.entity.Image
     */
    public StringBuilder createDockerfileContent(Project project,String login) {
        //todo:add user part
        StringBuilder dockerFileContent = new StringBuilder();
        boolean isInitialized = false;
        boolean hasExposedPort = false;
        String previousImageId = null;
        int imageCounter = 0;
        int i = 0;
        List<BuildStage> projectStages = project.getBuildStages();

        for (BuildStage stage : projectStages) {
            String line;
            if (!stage.getImage().get_id().equals(previousImageId)) {
                line = MessageFormat.format("FROM {0}:{1}", stage.getImage().getName(), stage.getVersion() != null ? stage.getVersion() : "latest");
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE)
                        .append(MessageFormat.format("WORKDIR  /usr/src/{0}",project.getName())).append(GeneralConstants.NEWLINE);
                if (previousImageId != null) {
                    line = MessageFormat.format("COPY --from={1} /usr/src/{0} .", project.getName(), imageCounter);
                    dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                }
            }
            if (!isInitialized) {
                line = MessageFormat.format("COPY ./{1}/{0} . ", project.getName(), systemConstant.getProjectFolderName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                line=MessageFormat.format("LABEL user=\"{0}\"",login);
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);

                line=MessageFormat.format("COPY ./{0} ../ ", systemConstant.getUtilsFolderName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                line= MessageFormat.format( "RUN  touch ../Changes ", systemConstant.getModifierScriptName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                line = MessageFormat.format("COPY ./{1}/{0}/{2} ../ ", login, systemConstant.getUserResourcesFolderName(),project.getName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                line= MessageFormat.format( "RUN  ../{0}.sh ../Changes ", systemConstant.getModifierScriptName());
                dockerFileContent.append(line).append(GeneralConstants.NEWLINE);
                isInitialized = true;
            }

            //integrate commands
            int j = 0;
            for (String command : stage.getCommand()) {

                line = command.replace("${mainClass}", project.getLaunchFilePath())
                        .replace("${projectName}",project.getName())
                        .replace("${userLogin}",login);
                line = MessageFormat.format("RUN {0}",line);
                if ((i == (projectStages.size() - 1)) && j == (stage.getCommand().size() - 1)) {
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
            previousImageId = stage.getImage().get_id();
            i++;
        }
        logger.debug(MessageFormat.format("created dockerfile for project where name is {0}  and user where login is {1}",project.getName(),login));
        return dockerFileContent;
    }


    /**
     * Write dockerfile instruction into file in {@code {@link SystemConstant}.getDockerfileFolderName()}
     *
     * @param filename filename of dockerfile.
     * @param dockerfileContent  Dockerfile instruction in {@link StringBuilder} instance.
     * @throws IOException throw if something went wrong in process of creating  dockerfile.
     *
     * @see Converter
     */
    public void writeTofile(String filename, StringBuilder dockerfileContent) throws IOException {

        String path = systemConstant.getPath().concat(systemConstant.getDockerfileFolderName().concat(GeneralConstants.SLASH)).concat(filename);
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
