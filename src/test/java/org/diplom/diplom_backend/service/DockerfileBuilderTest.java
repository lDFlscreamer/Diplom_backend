package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.LanguageConstant;
import org.diplom.diplom_backend.constant.SystemConstant;
import org.diplom.diplom_backend.entity.BuildStage;
import org.diplom.diplom_backend.entity.BuildTemplate;
import org.diplom.diplom_backend.entity.Image;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class DockerfileBuilderTest {

    @Autowired
    private DockerfileBuilder builder;
    @Autowired
    private SystemConstant systemConstant;

    @MockBean
    private ProjectRepository projectRepository;


    @Before
    public void setUp() {

        List<String> version = new ArrayList<>();
        version.add("3.6");
        version.add("3.3");
        version.add("3.2");
        Image maven = new Image("maven", version, " mvn exec:java -Dexec.mainClass=$(echo ${mainClass} | tr / .)", "mvn compile");
        List<BuildStage> buildStages = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        commands.add(maven.getCompileCommand());
        buildStages.add(new BuildStage(maven, maven.getVersion().get(0), commands));
        commands = new ArrayList<>();
        commands.add(maven.getExecuteCommand());
        buildStages.add(new BuildStage(maven, maven.getVersion().get(0), commands));
        BuildTemplate mavenApplication = new BuildTemplate("mavenApplication", LanguageConstant.JAVA, buildStages);
        ArrayList<Integer> ports = new ArrayList<>();
        ports.add(8080);
        Project mavenWord = new Project("test", "org/test/Application", mavenApplication.getStages(), ports);

        Mockito.when(projectRepository.findByName(mavenWord.getName()))
                .thenReturn(java.util.Optional.of(mavenWord));
    }

    @Test
    public void createDockerfileContent() {
        String name = "test";
        Optional<Project> found = projectRepository.findByName(name);
        assertTrue(found.isPresent());
        StringBuilder generatedContent = builder.createDockerfileContent(found.get(), "root");
        String expectedContent = "FROM maven:3.6\n" +
                "WORKDIR  /usr/src/test\n" +
                String.format("COPY ./%s/%s . \n", systemConstant.getProjectFolderName(), name) +
                String.format("LABEL user=\"%s\"\n", "root") +
                String.format("COPY ./%s ../ \n", systemConstant.getUtilsFolderName()) +
                "RUN  touch ../Changes \n" +
                String.format("COPY ./%s/%s/%s ../ \n", systemConstant.getUserResourcesFolderName(), "root", name) +
                String.format("RUN  ../%s.sh ../Changes \n", systemConstant.getModifierScriptName()) +
                "RUN mvn compile\n" +
                "EXPOSE 8080\n" +
                "ENTRYPOINT  mvn exec:java -Dexec.mainClass=$(echo org/test/Application | tr / .)\n";
        assertEquals(expectedContent, generatedContent.toString());
    }
}