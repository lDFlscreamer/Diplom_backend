package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.constant.GeneralConstants;
import org.diplom.diplom_backend.entity.Project;
import org.springframework.stereotype.Service;

@Service
public class Converter {

    public String getImageName(Project p,String login){
        return p.getProjectName().concat(GeneralConstants.UNDERSCORE).concat(login).toLowerCase();
    }
}
