package org.diplom.diplom_backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

/**
 * this class is represent baseImage .
 * Base Image use for launch and execute specific program(java ,python ,c++ & etc).
 *<p>Fields</p>
 * {@code name}  is a name of public docker image which can use for launch,execute project or modify project file
 *
 * {@code version} is list of string value that are explain versions of image
 *
 * {@code executeCommand} this template of command to execute something via this image
 *
 * {@code compileCommand} this template of command to compile something via this image
 * can be {@code null}
 *
 * @author Tverdokhlib
 * @see BuildStage
 * @see BuildTemplate
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Image {
    @Id
    private String id;
    private String name;
    private List<String> version;
    private String executeCommand;
    private String compileCommand;


    public Image() {
        this.id = UUID.randomUUID().toString();
    }

    public Image(String name, List<String> Version, String executeCommand, String compileCommand) {
        this();
        this.name = name;
        this.version = Version;
        this.executeCommand = executeCommand;
        this.compileCommand = compileCommand;
    }

    public Image(String name, List<String> Version, String executeCommand) {
        this();
        this.name = name;
        this.version = Version;
        this.executeCommand = executeCommand;
    }
}
