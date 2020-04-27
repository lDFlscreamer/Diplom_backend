package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.Language;
import org.diplom.diplom_backend.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

@Service
public class DockerfileBuilder {
    //todo:add logger
    @Autowired
    private LanguageRepository languageRepository;
    public boolean createDockerfile(String filename,String languageName, String mainClass, String projectPath) throws Exception{
        //todo:filename - projectName@UserName
        StringBuilder dockerFileContent = createDockerfileContent(languageName, mainClass, projectPath);
        return writeTofile(filename,dockerFileContent);
    }

    public StringBuilder createDockerfileContent(String languageName, String mainClass, String projectPath) throws Exception {
        StringBuilder dockerFile = new StringBuilder();
        Optional<Language> languageOptional = languageRepository.findByName(languageName);
        Language language;
        if (languageOptional.isPresent()) {
            language = languageOptional.get();
        } else {
            //todo: write a exception;
            throw new Exception();
        }
        //uses base image
        String baseImage = language.getBaseImage();
        dockerFile.append(MessageFormat.format("FROM {0}", baseImage)).append(GeneralConstants.NEWLINE);
        //set work directory
        dockerFile.append("WORKDIR  /usr/src/myapp").append(GeneralConstants.NEWLINE);
        //move source file
        String copySrcFile = MessageFormat.format("COPY . ./src ", projectPath).concat(GeneralConstants.NEWLINE);
        dockerFile.append(copySrcFile);
        if (language.getCompileCommand() != null) {

            dockerFile.append("RUN mkdir bin").append(GeneralConstants.NEWLINE);
            String mainClassLocalPath = mainClass.replace(".", "/");
            String compileCommand = "RUN  ".concat(MessageFormat.format(language.getCompileCommand(), "src".concat("/").concat(mainClassLocalPath)));
            dockerFile.append(compileCommand).append(GeneralConstants.NEWLINE);
        } else {
            mainClass = mainClass.replace(".", "/");

        }
        /*
            run application
         */
        String executeCommand = "CMD  ".concat(MessageFormat.format(language.getExecuteCommand(), mainClass));
        dockerFile.append(executeCommand).append(GeneralConstants.NEWLINE);
        return dockerFile;
    }

    public boolean writeTofile(String filename, StringBuilder dockerfileContent) {
        File myObj = new File(PathConstant.stringValue.concat(filename));
        try {
            myObj.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(myObj))) {
                writer.write(dockerfileContent.toString());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        return true;
    }
}
