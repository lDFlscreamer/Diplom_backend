package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;


/**
 * This class is represent a project configuration . And uses for launch project via docker.
 * Generate Dockerfile from buildStages.
 *
 * <p>Fields</p>
 * {@code name} uses for identify directory with project files
 *
 * {@code launchFilePath} uses for identify file with entry point
 *
 * {@code buildStages} is list of stages that are carried out to start the project
 *
 * @author Tverdokhlib
 * @see BuildStage
 *
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {
    @Id
    private String _id;
    private String name;//name of project
    private String language;
    private String path;
    private String launchFile;
    private String launchFilePath; //path to main file with entry point
    private List<String> users;
    private List<BuildStage> buildStages; // stages
    private List<Integer> ports;

    public Project() {
        this._id = UUID.randomUUID().toString();
    }

    public Project(String name, String launchFilePath, List<BuildStage> buildStages) {
        this();
        this.name = name;
        this.launchFilePath = launchFilePath;
        this.buildStages = buildStages;
    }

    public Project(String name, String launchFilePath, List<BuildStage> buildStages, List<Integer> ports) {
        this();
        this.name = name;
        this.launchFilePath = launchFilePath;
        this.buildStages = buildStages;
        this.ports = ports;
    }
}
