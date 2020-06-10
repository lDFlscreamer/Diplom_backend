package org.diplom.diplom_backend.entity;

import lombok.Data;
import org.diplom.diplom_backend.constant.LanguageConstant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

/**
 * this class represent a template for build standard project(java,maven,python etc)
 * <p>Fields</p>
 * {@code templateName} is template name .It`s short information about template .
 * {@code language} use for understand which programing language uses in this template
 * {@code stages} is a {@link BuildStage} for launch
 *
 * @author Tverdokhlib
 * @see BuildStage
 * @see BuildTemplate
 */
@Data
@Document(collection = "BuildTemplate")
public class BuildTemplate {
    @Id
    private String _id;
    private String templateName;
    private String language;
    private List<BuildStage> stages;

    public BuildTemplate() {
        this._id = UUID.randomUUID().toString();
    }

    public BuildTemplate(String templateName, String language, List<BuildStage> stages) {
        this();
        this.templateName = templateName;
        this.language = language;
        this.stages = stages;
    }

    public BuildTemplate(String templateName, LanguageConstant language, List<BuildStage> stages) {
        this();
        this.templateName = templateName;
        this.language = language.toString();
        this.stages = stages;
    }
}
