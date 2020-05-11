package org.diplom.diplom_backend.service.Dao;

import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProjectDao {

    @Autowired
    public ProjectRepository projectRepository;

    public Project getProjectById(String id) throws NoSuchElementException{
        Optional<Project> byId = projectRepository.findById(id);
        if (!byId.isPresent()){
            throw new NoSuchElementException(String.format("Project with id %s not found ",id));
        }
        return byId.get();
    }
}
