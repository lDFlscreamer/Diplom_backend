package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.service.Dao.ProjectDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ProjectLauncher.class);

    private HashMap<String, Process> launchedProjects = new HashMap<>();
    @Autowired
    private PathConstant pathConstant;
    @Autowired
    private WebSocketWritter webSocketWritter;
    @Autowired
    private Converter converter;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private DockerfileBuilder dockerfileBuilder;
    @Autowired
    private DockerImageCreater dockerImageCreater;


    public void addLaunchedProject(String imageName, Process p) {
        terminateProcess(imageName);
        launchedProjects.put(imageName, p);
    }


    public void terminateProcess(String imageName) {
        Process process = launchedProjects.get(imageName);
        if (process != null) {
            process.destroy();
            launchedProjects.remove(imageName);
            logger.info(MessageFormat.format("launched project with imageName  {0}  is terminated", imageName));
        }
    }

    public boolean inputInProject(String projectId, String userLogin, String input) throws NoSuchElementException {
        Project project = projectDao.getProjectById(projectId);
        String imageName = converter.getImageName(project, userLogin);
        Process process = launchedProjects.get(imageName);
        if (process == null) {
            logger.warn(MessageFormat.format("project with imageName  {0}  is not launched", imageName));
            return false;
        }
        OutputStream stdin = process.getOutputStream();
        try {
            stdin.write(input.concat(GeneralConstants.NEWLINE).getBytes());
            stdin.flush();
        } catch (IOException e) {
            logger.warn(MessageFormat.format("can`t  write into input stream of launched project with ImageName {0} ", imageName), e);
        }
        return true;
    }


    public String launchProject(Project project, String userLogin, String runCommand) throws NoSuchElementException {


        String imageName = converter.getImageName(project, userLogin);

        try {
            dockerfileBuilder.createDockerfile(imageName, project, userLogin);
        } catch (IOException e) {
            logger.warn(MessageFormat.format("cant  create dockerfile for  launch project with ImageName {0} ", imageName), e);
            return null;
        }
        if (!dockerImageCreater.createImage(imageName, pathConstant.getPath().concat(pathConstant.getDockerfileFolderName()).concat(imageName), pathConstant.getPath())) {
            return null;//todo:message
        }
        Process process = dockerImageCreater.runImage(imageName, runCommand);
        addLaunchedProject(imageName, process);
        return imageName;
    }

    public boolean stopProject(Project project, String userLogin) throws NoSuchElementException {
        String imageName = converter.getImageName(project, userLogin);
        this.terminateProcess(imageName);
        return true;
    }

    @Scheduled(fixedDelay = 100)
    public void sheduledOutput() {
        List<String> toTerminate = new ArrayList<>();
        launchedProjects.entrySet().stream().parallel().forEach(s -> {
            Process p = s.getValue();
            StringBuilder result = new StringBuilder();

            InputStream stderr = p.getErrorStream();
            InputStream stdout = p.getInputStream();

            try {
                int lastValue = 0;
                while (lastValue != stdout.available()) {
                    for (int i = stdout.available(); i > 0; i--) {
                        result.append((char) stdout.read());
                    }
                    lastValue = stdout.available();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                int lastValue = 0;
                while (lastValue != stderr.available()) {
                    for (int i = stderr.available(); i > 0; i--) {
                        result.append((char) stderr.read());
                    }
                    lastValue = stderr.available();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result.length() == 0 && !p.isAlive()) {

                String lastOutput = "end with" + p.exitValue() + GeneralConstants.NEWLINE;
                toTerminate.add(s.getKey());
                webSocketWritter.sendOutput(GeneralConstants.SLASH.concat(s.getKey()), lastOutput);
                return;
            }
            if (!result.toString().equals(GeneralConstants.EMPTY)) {
                webSocketWritter.sendOutput(GeneralConstants.SLASH.concat(s.getKey()), result.toString());
            }
        });
        toTerminate.stream().parallel().forEach(this::terminateProcess);
    }


}
