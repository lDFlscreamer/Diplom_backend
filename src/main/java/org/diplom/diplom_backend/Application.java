package org.diplom.diplom_backend;

import org.diplom.diplom_backend.constant.LanguageConstant;
import org.diplom.diplom_backend.constant.PathConstant;
import org.diplom.diplom_backend.entity.*;
import org.diplom.diplom_backend.repository.BuildTemplateRepository;
import org.diplom.diplom_backend.repository.ImageRepository;
import org.diplom.diplom_backend.repository.LanguageRepository;
import org.diplom.diplom_backend.repository.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BuildTemplateRepository buildTemplateRepository;


    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
        logger.info("start");
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
                Image java = new Image("openjdk", version, "java -cp ./target ${mainClass}", "javac -sourcepath ./src -d target $(echo ${mainClass} | tr / .).java");
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
                commands.add("mkdir target");
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
                Project pythonInput = new Project("pythonInput", "main", pythonApplication.getStages());
                ArrayList<Integer> ports = new ArrayList<>();
                ports.add(8080);
                Project mavenWord = new Project("test", "org/test/Application", mavenApplication.getStages(), ports);
                projectRepository.save(helloWord);
                projectRepository.save(mavenWord);
                projectRepository.save(pythonInput);
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


    boolean createFolder(String path) throws IllegalAccessException {
        File folder = new File(path);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                String message = String.format("folder can`t be created %s", PathConstant.path);
                logger.error(message);
                throw new IllegalAccessException(message);
            }
        }
        if (!folder.isDirectory()) {
            String message = String.format("folder is not a directory %s ", PathConstant.path);
            logger.error(message);
            throw new IllegalAccessException(message);
        }
        return true;
    }


}