package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {
    @Id
    private String id;
    private String projectName;
    private String mainClass;
    private List<BuildStage> buildStages;
    private List<Integer> ports;

    public Project() {
        this.id= UUID.randomUUID().toString();
    }

    public Project( String projectName, String mainClass, List<BuildStage> buildStages) {
        this();
        this.projectName = projectName;
        this.mainClass = mainClass;
        this.buildStages = buildStages;
    }

    public Project(String projectName, String mainClass, List<BuildStage> buildStages, List<Integer> ports) {
        this();
        this.projectName = projectName;
        this.mainClass = mainClass;
        this.buildStages = buildStages;
        this.ports = ports;
    }
}
