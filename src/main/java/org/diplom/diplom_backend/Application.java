package org.diplom.diplom_backend;

import org.diplom.diplom_backend.entity.Language;
import org.diplom.diplom_backend.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class Application {
    @Autowired
    LanguageRepository languageRepository;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (languageRepository.findAll().size() == 0) {
                languageRepository.save(new Language("java", "openjdk:7", "java -classpath ./bin {0}","javac -sourcepath ./src -d bin {0}.java"));
                languageRepository.save(new Language("python", "python:3", "python src/{0}.py"));
            }
        };
    }
}