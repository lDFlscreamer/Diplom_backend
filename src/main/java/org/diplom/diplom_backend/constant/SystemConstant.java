package org.diplom.diplom_backend.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SystemConstant {
    @Getter
    @Value("${path.directory.main:diplom/}")
    private  String path;
    @Getter
    @Value("${path.directory.dockerfile.folder.name:Dockerfile}")
    private  String DockerfileFolderName;
    @Getter
    @Value("${path.directory.project.folder.name:Project}")
    private  String ProjectFolderName;
    @Getter
    @Value("${path.directory.userResources.folder.name:User}")
    private  String UserResourcesFolderName;
    @Getter
    @Value("${path.directory.utils.folder.name:Utils}")
    private  String UtilsFolderName;
    @Getter
    @Value("${utils.script.modifier.name:projectModifier}")
    private  String modifierScriptName;
    @Getter
    private final String modifierScriptCode=
            "#!/bin/bash\n" +
                    "\n" +
                    "\t\n" +
                    "function addContentToProjectFile() {\n" +
                    "\techo \"$line $file\"\n" +
                    "\techo \"$line\">> $file\n" +
                    "}\n" +
                    "\n" +
                    "function modifyProject() {\n" +
                    "\t\n" +
                    "\tfile=\" \"\n" +
                    "\tadd=false\n" +
                    "\tremove=false\n" +
                    "\twhile IFS= read -r line; do\n" +
                    "\n" +
                    "\t\taddFile=$(echo $line | egrep -o \"^\\[\\+[[:space:]]([\\.\\/a-Z0-9]+)\\]$\" | egrep -o  \"([\\.\\/a-Z0-9]+)\")\n" +
                    "\t\tremoveFile=$(echo $line | egrep -o \"^\\[\\-[[:space:]]([\\.\\/a-Z0-9]+)\\]$\" | egrep -o  \"([\\.\\/a-Z0-9]+)\")\n" +
                    "\t\t\tif [ -n \"$addFile\" ];\n" +
                    "\t\t\tthen\n" +
                    "\t\t\t\tfile=$addFile\n" +
                    "\t\t\t\tadd=true\n" +
                    "\t\t\t\tremove=false\n" +
                    "\t\t\t\techo \"set add $file  $remove $add\"\n" +
                    "\n" +
                    "\t\t\telif [ -n \"$removeFile\" ]\n" +
                    "\t\t\tthen\n" +
                    "\t\t\t\tfile=$removeFile\n" +
                    "\t\t\t\tremove=true\n" +
                    "\t\t\t\tadd=false\n" +
                    "\t\t\t\trm -f $file\n" +
                    "\t\t\telif $remove \n" +
                    "\t\t\tthen\n" +
                    "\t\t\t\tcontinue\n" +
                    "\t\t\t\techo \"remove $file \"\n" +
                    "\t\t\telif  $add \n" +
                    "\t\t\tthen\n" +
                    "\t\t\t\t#echo \"add $add\"\n" +
                    "\t\t\t\taddContentToProjectFile \" $line\"  \n" +
                    "\t\t\telse \n" +
                    "\t\t\t\techo \"nothing\"\n" +
                    "\t\t\tfi\n" +
                    "\t\t\n" +
                    "\tdone<\"$1\"\n" +
                    "}\n" +
                    "\n" +
                    "modifyProject \"${1-.}\"";
}
