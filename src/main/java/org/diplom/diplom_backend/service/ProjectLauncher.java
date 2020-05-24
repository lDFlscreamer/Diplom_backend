package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.service.Dao.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProjectLauncher {
    private HashMap<String, Process> launchedProjects = new HashMap<>();
    // TODO: 5/24/20 logger
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
        }
    }

    public boolean inputInProject(String projectId, String userLogin, String input) throws NoSuchElementException {
        Project project = projectDao.getProjectById(projectId);
        String imageName = converter.getImageName(project, userLogin);
        Process process = launchedProjects.get(imageName);
        if (process == null) {
            //todo exception
            return false;
        }
        OutputStream stdin = process.getOutputStream();

        try {
            stdin.write(input.concat(GeneralConstants.NEWLINE).getBytes());
            stdin.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public String lounchProject(String projectId, String userLogin, String runCommand) throws NoSuchElementException {

        Project project = projectDao.getProjectById(projectId);
        String imageName = converter.getImageName(project, userLogin);

        if (!dockerfileBuilder.createDockerfile(imageName, project)) {
            //todo:throw exception
            return null;
        }
        //todo:filename - projectname_userlogin
        List<String> imageLogs = dockerImageCreater.createImage(imageName, PathConstant.path.concat(PathConstant.DockerfileFolderName).concat(imageName), PathConstant.path);
        Process process = dockerImageCreater.runImage(imageName, runCommand);
        addLaunchedProject(imageName, process);
        webSocketWritter.sendLogs("/".concat(imageName),String.join(GeneralConstants.NEWLINE,imageLogs));//todo: ? if needed
        return imageName;
    }

    public boolean stopProject(String projectId, String userLogin) throws NoSuchElementException {
        Project project = projectDao.getProjectById(projectId);
        String imageName = converter.getImageName(project, userLogin);
        this.terminateProcess(imageName);
        return true;
    }

    @Scheduled(fixedDelay = 100)
    public void sheduledOutput() {
        List<String> toTerminate=new ArrayList<>();
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

                String lastOutput = "end with" + p.exitValue()+"\n";
                toTerminate.add(s.getKey());
                webSocketWritter.sendOutput("/".concat(s.getKey()), lastOutput);
                return;
            }
            if (!result.toString().equals("")) {
                webSocketWritter.sendOutput("/".concat(s.getKey()), result.toString());
            }
        });
        toTerminate.stream().parallel().forEach(this::terminateProcess);
    }


}
