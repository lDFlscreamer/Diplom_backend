package org.diplom.diplom_backend.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PathConstant {
    @Getter
    @Value("${path.directory.main:diplom/}")
    private  String path;
    @Getter
    @Value("${path.directory.dockerfile.folder.name:Dockerfile/}")
    private  String DockerfileFolderName;
    @Getter
    @Value("${path.directory.project.folder.name:Project/}")
    private  String ProjectFolderName;
    @Getter
    @Value("${path.directory.userResources.folder.name:User/}")
    private  String UserResourcesFolderName;
}
