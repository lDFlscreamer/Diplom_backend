package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.SystemConstant;
import org.diplom.diplom_backend.entity.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

@Service
public class ProjectLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ProjectLauncher.class);

    private HashMap<String, Process> launchedProjects = new HashMap<>();
    @Autowired
    private SystemConstant systemConstant;
    @Autowired
    private WebSocketWritter webSocketWritter;
    @Autowired
    private Converter converter;
    @Autowired
    private ProjectDetailFinder projectDetailFinder;
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

    public boolean inputInProject(String imageName, String input) throws NoSuchElementException {
        Process process = launchedProjects.get(imageName);
        if (process == null) {
            webSocketWritter.sendOutput(imageName,input);
            webSocketWritter.sendOutput(imageName,GeneralConstants.NEWLINE.concat("!!! project may  not launched "));
            logger.warn(MessageFormat.format("project with imageName  {0}  is not launched\n", imageName));
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
        if (!dockerImageCreater.createImage(imageName, systemConstant.getPath().concat(systemConstant.getDockerfileFolderName().concat(GeneralConstants.SLASH)).concat(imageName), systemConstant.getPath())) {
            return null;//todo:message
        }
        Process process = dockerImageCreater.runImage(imageName, runCommand);
        addLaunchedProject(imageName, process);
        printPortData(imageName);
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

    public void printPortData(String imageName){
        Map<Integer, Integer> portData = projectDetailFinder.getPortData(imageName);
        if (!portData.isEmpty()) {
            String portStr = portData.values().stream().map(integer -> integer + "->" + integer).reduce((s, s2) -> s.concat(GeneralConstants.NEWLINE).concat(s2)).orElse(GeneralConstants.EMPTY);
            webSocketWritter.sendOutput(imageName, MessageFormat.format("Project port are redirected:".concat(GeneralConstants.NEWLINE).concat("{0}"), portStr));
        }
    }


}
