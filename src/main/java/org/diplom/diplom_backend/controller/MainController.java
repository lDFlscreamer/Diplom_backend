package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.repository.ProjectRepository;
import org.diplom.diplom_backend.service.DockerImageCreater;
import org.diplom.diplom_backend.service.DockerfileBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
public class MainController {
    @Autowired
    DockerImageCreater dockerImageCreater;
    @Autowired
    DockerfileBuilder dockerfileBuilder;
    @Autowired
    ProjectRepository projectRepository;


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

}
