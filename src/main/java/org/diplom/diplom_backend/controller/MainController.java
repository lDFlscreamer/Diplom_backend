package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.service.DockerImageCreater;
import org.diplom.diplom_backend.service.DockerfileBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MainController {
    @Autowired
    DockerImageCreater dockerImageCreater;
    @Autowired
    DockerfileBuilder dockerfileBuilder;

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

    @GetMapping(value = "/images")
    public String getImages(
            @RequestBody(required = false) Map<String, Object> args) {
        return dockerImageCreater.getImages();
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

        return dockerImageCreater.runImage(image, command);
    }

    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String executeProject(
            @RequestBody Map<String, Object> args) throws Exception {
        Object projectPathObj = args.get("projectPath");
        Object languageObj = args.get("language");
        Object mainClassObj = args.get("mainClass");

        String language = languageObj == null ? null : languageObj.toString();
        String projectPath = projectPathObj == null ? null : projectPathObj.toString();
        String mainClass = mainClassObj == null ? null : mainClassObj.toString();

        return dockerfileBuilder.createBaseDockerFile(language,  mainClass,projectPath).toString();
    }
}
