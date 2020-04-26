package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Language {
    @Id
    private  String id;
    private String name;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private String baseImage;
    private String executeCommand;
    private String compileCommand;

    public Language() {
        this.id= UUID.randomUUID().toString();
    }

    public Language(String name, String baseImage, String executeCommand) {
        this();
        this.name = name;
        this.baseImage = baseImage;
        this.executeCommand = executeCommand;
    }

    public Language(String name, String baseImage, String executeCommand,  String compileCommand) {
        this();
        this.name = name;
        this.baseImage = baseImage;
        this.executeCommand = executeCommand;
        this.compileCommand = compileCommand;
    }
}
