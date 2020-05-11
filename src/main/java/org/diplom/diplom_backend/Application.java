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
                Image java = new Image("openjdk", version, "java -classpath ./bin ${mainClass}", "javac -sourcepath ./src -d bin $(echo ${mainClass} | tr / .).java");
                //create python Image
                version = new ArrayList<>();
                version.add("3");
                version.add("2.7");
                version.add("2");
                Image python = new Image("python", version, "python ${mainClass}.py");
                //create gcc Image
                version = new ArrayList<>();
                version.add("9");
                version.add("8");
                version.add("7");
                version.add("6");
                Image gcc = new Image("gcc", version, "./bin/${mainClass}", "g++ -o bin/${mainClass}  ${mainClass}");
                //create maven
                version = new ArrayList<>();
                version.add("3.6");
                version.add("3.3");
                version.add("3.2");
                Image maven = new Image("maven", version, " mvn exec:java -Dexec.mainClass=$(echo ${mainClass} | tr / .)", "mvn compile");

                /*
                 * save to data base
                 * */
                imageRepository.save(java);
                imageRepository.save(python);
                imageRepository.save(gcc);
                imageRepository.save(maven);
                /*create Language*/
                //create java Language
                List<Image> images = new ArrayList<>();
                images.add(java);
                images.add(maven);

                Language javaLanguage = new Language(LanguageConstant.JAVA, images);
                //create python Language
                images = new ArrayList<>();
                images.add(python);

                Language pythonLanguage = new Language(LanguageConstant.PYTHON, images);
                //create cpp Language
                images = new ArrayList<>();
                images.add(gcc);

                Language CppLanguage = new Language(LanguageConstant.Cpp, images);
                /*
                 * save to data base
                 * */
                languageRepository.save(javaLanguage);
                languageRepository.save(pythonLanguage);
                languageRepository.save(CppLanguage);
                /*
                 * create building template
                 * */
                //java
                List<BuildStage> buildStages = new ArrayList<>();
                List<String> commands = new ArrayList<>();
                commands.add("mkdir bin");
                commands.add(java.getCompileCommand());
                buildStages.add(new BuildStage(java, java.getImageVersion().get(0), commands));
                commands = new ArrayList<>();
                commands.add(java.getExecuteCommand());
                buildStages.add(new BuildStage(java, java.getImageVersion().get(0), commands));
                BuildTemplate javaApplication = new BuildTemplate("JavaApplication", LanguageConstant.JAVA, buildStages);
                //maven
                buildStages = new ArrayList<>();
                commands = new ArrayList<>();
                commands.add(maven.getCompileCommand());
                buildStages.add(new BuildStage(maven, maven.getImageVersion().get(0), commands));
                commands = new ArrayList<>();
                commands.add(maven.getExecuteCommand());
                buildStages.add(new BuildStage(maven, maven.getImageVersion().get(0), commands));
                BuildTemplate mavenApplication = new BuildTemplate("mavenApplication", LanguageConstant.JAVA, buildStages);
                //Python
                buildStages = new ArrayList<>();
                commands = new ArrayList<>();
                commands.add(python.getExecuteCommand());
                buildStages.add(new BuildStage(python, python.getImageVersion().get(0), commands));
                BuildTemplate pythonApplication = new BuildTemplate("PythonApplication", LanguageConstant.PYTHON, buildStages);
                //cpp
                buildStages = new ArrayList<>();
                commands = new ArrayList<>();
                commands.add("mkdir bin");
                commands.add(gcc.getCompileCommand());
                buildStages.add(new BuildStage(gcc, gcc.getImageVersion().get(0), commands));
                commands = new ArrayList<>();
                commands.add(gcc.getExecuteCommand());
                buildStages.add(new BuildStage(gcc, gcc.getImageVersion().get(0), commands));
                BuildTemplate CppApplication = new BuildTemplate("CppApplication", LanguageConstant.Cpp, buildStages);
                /*
                 * save to dataBase
                 * */
                buildTemplateRepository.save(javaApplication);
                buildTemplateRepository.save(mavenApplication);
                buildTemplateRepository.save(pythonApplication);
                buildTemplateRepository.save(CppApplication);
                /*
                 * create test project
                 * //todo:delete on production
                 * */
                Project helloWord = new Project("helloWord", "Main", javaApplication.getStages());
                ArrayList<Integer> ports = new ArrayList<>();
                ports.add(8080);
                Project mavenWord = new Project("test", "org/test/Application", mavenApplication.getStages(),ports);
                projectRepository.save(helloWord);
                projectRepository.save(mavenWord);
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


    boolean createFolder(String path) {
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