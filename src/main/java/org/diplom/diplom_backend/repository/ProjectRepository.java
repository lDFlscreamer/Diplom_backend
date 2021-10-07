package org.diplom.diplom_backend.repository;

import org.bson.types.ObjectId;
import org.diplom.diplom_backend.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, ObjectId> {
	Optional<Project> findByName(String name);

	Optional<Project> findByNameAndLaunchFilePath(String projectName, String mainClass);
}
