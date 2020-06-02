package org.diplom.diplom_backend.repository;

import org.diplom.diplom_backend.entity.BuildTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildTemplateRepository extends MongoRepository<BuildTemplate, String> {
    Iterable<BuildTemplate> findByLanguage(String language);
    Optional<BuildTemplate> findByTemplateName(String name);
}
