package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {
    @Id
    private String id;
    private String projectName;
    private String mainClass;
    private List<BuildStage> buildStages;

    public Project() {
        this.id= UUID.randomUUID().toString();
    }

    public Project( String projectName, String mainClass, List<BuildStage> buildStages) {
        this();
        this.projectName = projectName;
        this.mainClass = mainClass;
        this.buildStages = buildStages;
    }
}
