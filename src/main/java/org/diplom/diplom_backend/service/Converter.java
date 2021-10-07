package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.entity.Project;
import org.springframework.stereotype.Service;


/**
 * represent a method for generate identifier of launched project
 *
 * @author Tverdokhlib
 */
@Service
public class Converter {

    private final String DELIMITER =GeneralConstants.UNDERSCORE;

    /**
     * method to convert data from {@link Project} and {@code user login } to identifier of launched project
     * @param project exemplar of {@link Project}
     * @param login user login to identify which user run this project
     * @return identifier of launched project.Which uses to name dockerImage or identify launched project in map of launched project
     */
    public String getImageName(Project project,String login){
        return project.getName().concat(DELIMITER).concat(login).toLowerCase();
    }


}
