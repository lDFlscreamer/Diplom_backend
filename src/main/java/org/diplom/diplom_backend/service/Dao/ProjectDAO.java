package org.diplom.diplom_backend.service.Dao;

import org.bson.types.ObjectId;
import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Data Access Object for Project instance from data base
 */
@Service
public class ProjectDAO {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDAO.class);

	@Autowired
	public ProjectRepository projectRepository;

	/**
	 * Get project instance by project id
	 *
	 * @param id id of project
	 * @return project instance from db
	 * @throws NoSuchElementException throw if project with {@code id} not found
	 * @see Project
	 */
	public Project getProjectById(ObjectId id) throws NoSuchElementException {
		Optional<Project> byId = projectRepository.findById(id);
		if (!byId.isPresent()) {
			logger.warn(MessageFormat.format("Project with id {0} not found ", id));
			throw new NoSuchElementException(String.format("Project with id %s not found ", id));
		}
		return byId.get();
	}

	/**
	 * Get project instance by project (Map) id
	 *
	 * @param id id of project
	 * @return project instance from db
	 * @throws NoSuchElementException throw if project with {@code id} not found
	 * @see Project
	 */
	public Project getProjectByStringId(String id) throws NoSuchElementException {
		ObjectId projectId = new ObjectId(id);
		return getProjectById(projectId);
	}

	/**
	 * Get project instance by projectName
	 *
	 * @param name name of project
	 * @return project instance from db
	 * @throws NoSuchElementException throw if project with {@code projectName} not found
	 * @see Project
	 */
	public Project getProjectByName(String name) throws NoSuchElementException {

		Optional<Project> byName = projectRepository.findByName(name);
		if (!byName.isPresent()) {
			logger.warn(MessageFormat.format("Project with name {0} not found ", name));
			throw new NoSuchElementException(String.format("Project with id %s not found ", name));
		}
		return byName.get();
	}


}
