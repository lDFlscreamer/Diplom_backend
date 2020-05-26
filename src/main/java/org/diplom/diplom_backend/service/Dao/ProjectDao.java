package org.diplom.diplom_backend.service.Dao;

import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProjectDao {
    private static final Logger logger = LoggerFactory.getLogger(ProjectDao.class);

    @Autowired
    public ProjectRepository projectRepository;

    public Project getProjectById(String id) throws NoSuchElementException{
        Optional<Project> byId = projectRepository.findById(id);
        if (!byId.isPresent()){
            logger.error(MessageFormat.format("Project with id {0} not found ",id));
            throw new NoSuchElementException(String.format("Project with id %s not found ",id));
        }
        return byId.get();
    }
}
