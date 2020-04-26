package org.diplom.diplom_backend.repository;

import org.diplom.diplom_backend.entity.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends MongoRepository<Language, String> {
    Optional<Language> findByName(String name);
}
