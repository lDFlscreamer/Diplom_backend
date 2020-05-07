package org.diplom.diplom_backend.repository;

import org.diplom.diplom_backend.constant.LanguageConstant;
import org.diplom.diplom_backend.entity.BuildTemplate;
import org.diplom.diplom_backend.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildTemplateRepository extends MongoRepository<BuildTemplate, String> {
    Optional<Project> findAllById(String id);
    Iterable<Project> findByLanguage(String language);
}
