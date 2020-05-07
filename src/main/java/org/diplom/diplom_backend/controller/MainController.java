package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.constant.LanguageConstant;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.Image;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ImageRepository;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.diplom.diplom_backend.service.DockerImageCreater;
import org.diplom.diplom_backend.service.DockerfileBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class MainController {
    @Autowired
    DockerImageCreater dockerImageCreater;
    @Autowired
    DockerfileBuilder dockerfileBuilder;
    @Autowired
    ProjectRepository projectRepository;

    //todo:javadoc
    @PostMapping(value = "/createImage")
    public List<String> create(
            @RequestBody(required = false) Map<String, Object> args) {
        Object nameObj = args.get("name");
        Object dockerfilePathObj = args.get("dockerfilePath");
        Object projectPathObj = args.get("projectPath");

        String name = nameObj == null ? null : nameObj.toString();
        String dockerfilePath = dockerfilePathObj == null ? null : dockerfilePathObj.toString();
        String projectPath = projectPathObj == null ? null : projectPathObj.toString();
        return dockerImageCreater.createImage(name, dockerfilePath, projectPath);
    }


    @PostMapping(value = "/run",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String runImage(
            @RequestBody Map<String, Object> args) {
        Object imageObj = args.get("image");
        Object commandObj = args.get("command");
        String image = imageObj == null ? null : imageObj.toString();
        String command = commandObj == null ? null : commandObj.toString();

        return dockerImageCreater.runImage(image, command).toString();
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
//todo:write code
        Project project = projectRepository.findById(projectId).orElse(null);
        if(project==null){
            //exception
            // todo: error message
            return "error";
        }
        String imageName = project.getProjectName().concat("_").concat(userLogin).toLowerCase();

        if(!dockerfileBuilder.createDockerfile(imageName,project)){
            //todo:throw exception
            return "ERROr";
        }
        //todo:filename - projectName@UserName
        List<String> result= new ArrayList<>();
       result.addAll(dockerImageCreater.createImage(imageName,PathConstant.path.concat(PathConstant.DockerfileFolderName).concat(imageName),PathConstant.path));
        result.addAll(dockerImageCreater.runImage(imageName,runCommand));
        return result.toString();

    }
}
