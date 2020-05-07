package org.diplom.diplom_backend;

import org.diplom.diplom_backend.constant.LanguageConstant;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.*;
import org.diplom.diplom_backend.repository.BuildTemplateRepository;
import org.diplom.diplom_backend.repository.ImageRepository;
import org.diplom.diplom_backend.repository.LanguageRepository;
import org.diplom.diplom_backend.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

    //todo:add logger
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    BuildTemplateRepository buildTemplateRepository;


    public static void main(String[] args) {

            SpringApplication app = new SpringApplication(Application.class);
            app.run(args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            /*
            * validate DataBase
            * */
            if (imageRepository.findAll().size() == 0) {
                /*
                drop all collection
                 */
                projectRepository.deleteAll();
                imageRepository.deleteAll();
                languageRepository.deleteAll();
                /*
                * create Images
                * */
                //create java Image
                List<String> version = new ArrayList<>();
                version.add("13");
                version.add("12");
                version.add("11");
                version.add("10");
                version.add("9");
                version.add("8");
                version.add("7");
                Image java = new Image("openjdk", version, "java -classpath ./bin ${mainClass}", "javac -sourcepath ./src -d bin ${mainClass}.java");
                //create python Image
                version = new ArrayList<>();
                version.add("3");
                version.add("2.7");
                version.add("2");
                Image python = new Image("python", version, "python src/${mainClass}.py");
                //create gcc Image
                version = new ArrayList<>();
                version.add("9");
                version.add("8");
                version.add("7");
                version.add("6");
                Image gcc = new Image("gcc", version, "python src/${mainClass}.py");
                /*
                * save to data base
                * */
                imageRepository.save(java);
                imageRepository.save(python);
                /*create Language*/
                //create java Language
                List<Image> images = new ArrayList<>();
                images.add(java);

                Language javaLanguage = new Language(LanguageConstant.JAVA, images);
                //create python Language
                images = new ArrayList<>();
                images.add(python);

                Language pythonLanguage = new Language(LanguageConstant.PYTHON, images);
                languageRepository.save(javaLanguage);
                languageRepository.save(pythonLanguage);
                /*
                * create building template
                * */
                List<BuildStage> buildStages=new ArrayList<>();
                List<String> commands=new ArrayList<>();
                commands.add("mkdir bin");
                commands.add(java.getCompileCommand());
                buildStages.add(new BuildStage(java,java.getImageVersion().get(0),commands));
                commands=new ArrayList<>() ;
                commands.add(java.getExecuteCommand());
                buildStages.add(new BuildStage(java,java.getImageVersion().get(0),commands));
                BuildTemplate javaApplication = new BuildTemplate("JavaApplication", LanguageConstant.JAVA, buildStages);
                /*
                * save to dataBase
                * */
                buildTemplateRepository.save(javaApplication);
                /*
                * create test project
                * //todo:delete on production
                * */
                Project helloWord = new Project("helloWord", "Main", javaApplication.getStages());
                projectRepository.save(helloWord);
            }

            /*
            * validate path to work directory
            * */
            createFolder(PathConstant.path);
            createFolder(PathConstant.path.concat(PathConstant.DockerfileFolderName));
            createFolder(PathConstant.path.concat(PathConstant.ProjectFolderName));
            createFolder(PathConstant.path.concat(PathConstant.UserResourcesFolderName));
        };
    }


    boolean createFolder(String path)  {
        File folder = new File(path);
        if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    //todo:write exception
                    System.out.println(MessageFormat.format("folder can`t be created {0}", PathConstant.path));
                    return false;
                }
        }
        if (!folder.isDirectory()) {
            //todo:write exception
            System.out.println(MessageFormat.format("folder is not a directory {0} ", PathConstant.path));
            return false;
        }
        return true;
    }


}