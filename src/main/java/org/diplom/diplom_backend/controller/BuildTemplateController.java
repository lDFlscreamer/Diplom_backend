package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.entity.BuildTemplate;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.BuildTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "buildTemplate")
public class BuildTemplateController {

    @Autowired
    BuildTemplateRepository buildTemplateRepository;


    @GetMapping(value = "/{Language}")
    @ResponseStatus(value = HttpStatus.FOUND)
    public Iterable<BuildTemplate> getTemplateByLangugae(@PathVariable("Language") String language){
       return buildTemplateRepository.findByLanguage(language);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.FOUND)
    public List<BuildTemplate> findAll(){
        return buildTemplateRepository.findAll();
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Project createProject(@RequestBody Map<String, Object> project){


        return null;
    }

}
