package org.diplom.diplom_backend.entity;

import lombok.Data;
import org.diplom.diplom_backend.constant.LanguageConstant;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Data
public class BuildTemplate {
    @Id
    private String id;
    private String templateName;
    private String language;
    private List<BuildStage> stages;

    public BuildTemplate() {
        this.id = UUID.randomUUID().toString();
    }

    public BuildTemplate(String templateName, String language, List<BuildStage> stages) {
        this.templateName = templateName;
        this.language = language;
        this.stages = stages;
    }

    public BuildTemplate(String templateName, LanguageConstant language, List<BuildStage> stages) {
        this.templateName = templateName;
        this.language = language.toString();
        this.stages = stages;
    }
}
