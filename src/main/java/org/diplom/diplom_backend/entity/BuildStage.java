package org.diplom.diplom_backend.entity;

import lombok.Data;

import java.util.List;

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
