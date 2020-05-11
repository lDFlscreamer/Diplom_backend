package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.diplom.diplom_backend.service.ProjectDetailFinder;
import org.diplom.diplom_backend.service.ProjectLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(value ="/project")
public class ProjectConroller {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private ProjectLauncher projectLauncher;
    @Autowired
    private ProjectDetailFinder projectDetailFinder;


    @GetMapping(value = "/{Name}")
    @ResponseStatus(value = HttpStatus.FOUND)
    public Project getProjectByName(@PathVariable("Name") String name){
        Optional<Project> byProjectName = projectRepository.findByProjectName(name);
        return byProjectName.orElse(null);
    }
    @GetMapping
    @ResponseStatus(value = HttpStatus.FOUND)
    public List<Project> findAll(){
        return projectRepository.findAll();
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Project createProject(@RequestBody Project project){
        Optional<Project> byProjectNameAndMainClass = projectRepository.findByProjectNameAndMainClass(project.getProjectName(), project.getMainClass());
        return byProjectNameAndMainClass.orElseGet(() -> projectRepository.save(project));
    }

    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String executeProject(
            @RequestBody Map<String, Object> args) throws Exception {
        Object projectIdObj = args.get("projectId");
        Object userLoginObj = args.get("userLogin");

        String projectId = projectIdObj.toString();
        String userLogin = userLoginObj.toString();
        String runCommand=null;
        List<String> strings = projectLauncher.lounchProject(projectId, userLogin, runCommand);
        strings.add(projectLauncher.getOutPutFromProject(projectId,userLogin));
        //todo write right
        return String.join(GeneralConstants.SPACE,strings);
    }

    @PostMapping(value = "/stop",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String stopLaunchedProject(
            @RequestBody Map<String, Object> args) throws NoSuchElementException {
        Object projectIdObj = args.get("projectId");
        Object userLoginObj = args.get("userLogin");

        String projectId = projectIdObj.toString();
        String userLogin = userLoginObj.toString();
       return projectLauncher.stopProject(projectId,userLogin)?"DONE":"FAIL";
    }

    @PostMapping(value = "/port",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Map<Integer, Integer> PortLaunchedProject(
            @RequestBody Map<String, Object> args) throws NoSuchElementException {
        Object projectIdObj = args.get("projectId");
        Object userLoginObj = args.get("userLogin");

        String projectId = projectIdObj.toString();
        String userLogin = userLoginObj.toString();
        return projectDetailFinder.getPortData(projectId,userLogin);
    }

    @PostMapping(value = "/outPut",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String outPutProject(
            @RequestBody Map<String, Object> args) throws NoSuchElementException {
        Object projectIdObj = args.get("projectId");
        Object userLoginObj = args.get("userLogin");

        String projectId = projectIdObj.toString();
        String userLogin = userLoginObj.toString();
        return projectLauncher.getOutPutFromProject(projectId,userLogin);
    }

    @PostMapping(value = "/inPut",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public boolean inPutProject(
            @RequestBody Map<String, Object> args) throws NoSuchElementException {
        Object projectIdObj = args.get("projectId");
        Object userLoginObj = args.get("userLogin");
        Object inputObj = args.get("input");

        String projectId = projectIdObj.toString();
        String userLogin = userLoginObj.toString();
        String input = inputObj.toString();
        return projectLauncher.inputInProject(projectId,userLogin,input);
    }
}
