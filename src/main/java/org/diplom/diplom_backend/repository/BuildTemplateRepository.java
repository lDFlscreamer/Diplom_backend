package org.diplom.diplom_backend.repository;

import org.diplom.diplom_backend.entity.BuildTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildTemplateRepository extends MongoRepository<BuildTemplate, String> {
    List<BuildTemplate> findByLanguage(String language);
    Optional<BuildTemplate> findByTemplateName(String name);
}
