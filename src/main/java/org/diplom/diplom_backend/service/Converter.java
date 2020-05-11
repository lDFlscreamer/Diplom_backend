package org.diplom.diplom_backend.service;

import org.diplom.diplom_backend.entity.Project;
import org.springframework.stereotype.Service;

@Service
public class Converter {

    public String getImageName(Project p,String login){
        // TODO: 5/11/20 throw exception
        return p.getProjectName().concat("_").concat(login).toLowerCase();
    }
}
