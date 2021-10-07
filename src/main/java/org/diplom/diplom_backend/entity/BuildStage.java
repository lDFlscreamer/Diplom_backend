package org.diplom.diplom_backend.entity;

import lombok.Data;

import java.util.List;

/**
 * {@code BuildStage} uses to describe one build step
 * <p>Fields</p>
 * {@code image}  is exemplar of {@link Image} entity.Describes image dependency for this stage.
 * {@code version} is version of image .
 * {@code command} this sis list of command which must be executed on this step
 *
 * @author tverdokhlib
 * @see BuildTemplate
 * @see BuildStage
 * @see Image
 */
@Data
public class BuildStage {
    private Image image;
    private String version;
    private List<String> command;

    public BuildStage(Image image, String version, List<String> command) {
        this.image = image;
        this.version = version;
        this.command = command;
    }
}
