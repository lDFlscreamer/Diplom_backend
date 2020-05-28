package org.diplom.diplom_backend.repository;

import org.diplom.diplom_backend.entity.Language;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LanguageRepository extends MongoRepository<Language, String> {
    Optional<Language> findByLanguage(String language);

}
