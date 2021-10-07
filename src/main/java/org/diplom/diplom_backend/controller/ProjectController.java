package org.diplom.diplom_backend.controller;

import org.diplom.diplom_backend.entity.Project;
import org.diplom.diplom_backend.repository.ProjectRepository;
import org.diplom.diplom_backend.service.Converter;
import org.diplom.diplom_backend.service.Dao.ProjectDAO;
import org.diplom.diplom_backend.service.DockerCleaner;
import org.diplom.diplom_backend.service.ProjectDetailFinder;
import org.diplom.diplom_backend.service.ProjectLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	DockerCleaner dockerCleaner;
	@Autowired
	private Converter converter;
	@Autowired
	private ProjectLauncher projectLauncher;
	@Autowired
	private ProjectDAO projectDao;
	@Autowired
	private ProjectDetailFinder projectDetailFinder;

	@GetMapping(value = "/{Name}")
	@ResponseStatus(value = HttpStatus.FOUND)
	public Project getProjectByName(@PathVariable("Name") String name) {
		Optional<Project> byProjectName = projectRepository.findByName(name);
		return byProjectName.orElse(null);
	}

	@GetMapping
	@ResponseStatus(value = HttpStatus.FOUND)
	public List<Project> findAll() {
		return projectRepository.findAll();
	}

	@PutMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public Project createProject(@RequestBody Project project) {
		Optional<Project> byProjectNameAndMainClass = projectRepository.findByNameAndLaunchFilePath(project.getName(), project.getLaunchFilePath());
		return byProjectNameAndMainClass.orElseGet(() -> projectRepository.save(project));
	}

	@PostMapping(value = "/execute",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public String executeProject(
			@RequestBody Map<String, Object> args) {
		Object projectIdObj = args.get("projectId");
		Object userLoginObj = args.get("userLogin");
		Object runCommandObj = args.get("runCommand");

		String projectId = projectIdObj.toString();
		String userLogin = userLoginObj.toString();

		Project project = projectDao.getProjectByStringId(projectId);
		return projectLauncher.launchProject(project, userLogin, runCommandObj == null ? null : runCommandObj.toString());
	}

	@PostMapping(value = "/stop",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public String stopLaunchedProject(
			@RequestBody Map<String, Object> args) throws NoSuchElementException {
		Object projectIdObj = args.get("projectId");
		Object userLoginObj = args.get("userLogin");

		String projectId = projectIdObj.toString();
		String userLogin = userLoginObj.toString();
		Project project = projectDao.getProjectByStringId(projectId);

		return projectLauncher.stopProject(project, userLogin) ? "DONE" : "FAIL";
	}

	@PostMapping(value = "/port",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public Map<Integer, Integer> PortLaunchedProject(
			@RequestBody Map<String, Object> args) throws NoSuchElementException {
		Object projectIdObj = args.get("projectId");
		Object userLoginObj = args.get("userLogin");

		String projectId = projectIdObj.toString();
		String userLogin = userLoginObj.toString();
		Project project = projectDao.getProjectByStringId(projectId);
		String imageName = converter.getImageName(project, userLogin);
		return projectLauncher.printPortData(imageName);
	}

	@MessageMapping("/{imageName}/input")
	public void inputInProject(@DestinationVariable String imageName, String message) {
		try {
			projectLauncher.inputInProject(imageName, message);
		} catch (NoSuchElementException e) {
			logger.info(MessageFormat.format("can not input {1} .Because Project with imageName {0} may not be launched", imageName, message));
		}
	}

	@PostMapping(value = "/logOut",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void logOut(
			@RequestBody Map<String, Object> args) throws NoSuchElementException {
		Object userLoginObj = args.get("userLogin");

		String userLogin = userLoginObj.toString();
		dockerCleaner.removeImages(userLogin);
	}
}
