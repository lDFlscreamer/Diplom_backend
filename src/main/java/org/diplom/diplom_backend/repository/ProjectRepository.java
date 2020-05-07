package org.diplom.diplom_backend.repository;

import com.mongodb.Mongo;
import org.diplom.diplom_backend.entity.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findById(String id);
    Optional<Project> findByProjectName(String name);
}
