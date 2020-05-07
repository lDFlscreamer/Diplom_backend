package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Image {
    @Id
    private String id;
    private String imageName;
    private List<String> imageVersion;
    private String executeCommand;
    private String compileCommand;


    public Image() {
        this.id = UUID.randomUUID().toString();
    }

    public Image(String imageName, List<String> imageVersion, String executeCommand, String compileCommand) {
        this();
        this.imageName = imageName;
        this.imageVersion = imageVersion;
        this.executeCommand = executeCommand;
        this.compileCommand = compileCommand;
    }

    public Image(String imageName, List<String> imageVersion, String executeCommand) {
        this();
        this.imageName = imageName;
        this.imageVersion = imageVersion;
        this.executeCommand = executeCommand;
    }
}
