package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.Language;
import org.diplom.diplom_backend.repository.LanguageRepository;
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
    LanguageRepository languageRepository;

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
        return dockerImageCreater.getImages().toString();
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
        Object projectPathObj = args.get("projectPath");
        Object languageObj = args.get("language");
        Object mainClassObj = args.get("mainClass");
        Object runCommandObj = args.get("runCommand");
        if(projectPathObj==null || languageObj == null ||mainClassObj == null){
            return "empty one of required fields";
        }
        String language = languageObj.toString();
        String projectPath = projectPathObj.toString();
        String mainClass = mainClassObj.toString();
        String runCommand ;

        if(runCommandObj==null){
            Optional<Language> languageOpt = languageRepository.findByName(language);
            if(!languageOpt.isPresent())
            {
                return "language not found";
                //todo:throw exception
            }
            runCommand=languageOpt.get().getExecuteCommand();
        }else
        {
            runCommand=runCommandObj.toString();
        }
        runCommand= MessageFormat.format(runCommand,mainClass);

        String imageName = language.concat("_").concat(mainClass).toLowerCase();
        //todo: error message
        if(!dockerfileBuilder.createDockerfile(imageName,language,  mainClass,projectPath)){
            //todo:throw exception
            return "ERROr";
        }
        //todo:filename - projectName@UserName
        List<String> result= new ArrayList<>();
        result.addAll(dockerImageCreater.createImage(imageName,PathConstant.stringValue.concat(imageName),projectPath));
        result.addAll(dockerImageCreater.runImage(imageName,runCommand));
        return result.toString();

    }
}
