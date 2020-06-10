package org.diplom.diplom_backend.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemConstant {
    /**
     * path to folder which will be contains all system directory
     */
    @Getter
    @Value("${path.directory.main:diplom/}")
    private  String path;
    /**
     * folder name which will be contains all dockerfile
     */
    @Getter
    @Value("${path.directory.dockerfile.folder.name:Dockerfile}")
    private  String DockerfileFolderName;
    /**
     * folder name which will be contains all existing project
     */
    @Getter
    @Value("${path.directory.project.folder.name:Project}")
    private  String ProjectFolderName;
    /**
     * folder name which will be contains user data
     */
    @Getter
    @Value("${path.directory.userResources.folder.name:User}")
    private  String UserResourcesFolderName;
    /**
     * folder name which will be contains all utils bash script
     */
    @Getter
    @Value("${path.directory.utils.folder.name:Utils}")
    private  String UtilsFolderName;
    /**
     * folder name which will be contains all utils bash script
     */
    @Getter
    @Value("${userResources.changes.file.name:Changes.txt}")
    private  String changesFilename;
    /**
     * script name of modifier project
     */
    @Getter
    @Value("${utils.script.modifier.name:projectModifier}")
    private  String modifierScriptName;
    /**
     * script code of modifier project
     */
    @Getter
    private final String modifierScriptCode=
            "#!/bin/bash\n" +
                    "\t\n" +
                    "function addContentToProjectFile() {\n" +
                    "\tpart1=`dirname \"$file\"`\n" +
                    "\tpart2=`basename \"$file\"`\n" +
                    "\tmkdir -p \"$part1\"\n" +
                    "\tif [ ! -f \"$file\" ]; then\n" +
                    "\t touch $file\n" +
                    "\tfi\n" +
                    "\t#echo \"$line $file\"\n" +
                    "\techo \"$line\">> $file\n" +
                    "}\n" +
                    "\n" +
                    "function modifyProject() {\n" +
                    "\t\n" +
                    "\tfile=\" \"\n" +
                    "\tadd=false\n" +
                    "\tremove=false\n" +
                    "\twhile IFS= read -r line; do\n" +
                    "\t\taddFile=$(echo $line | egrep -o \"^\\[\\+[[:space:]]([\\.\\/a-Z0-9]+)\\]$\" | egrep -o  \"([\\.\\/a-Z0-9]+)\")\n" +
                    "\t\tremoveFile=$(echo $line | egrep -o \"^\\[\\-[[:space:]]([\\.\\/a-Z0-9]+)\\]$\" | egrep -o  \"([\\.\\/a-Z0-9]+)\")\n" +
                    "\t\t\tif [ -n \"$addFile\" ];\n" +
                    "\t\t\tthen\n" +
                    "\t\t\t\tfile=$addFile\n" +
                    "\t\t\t\tadd=true\n" +
                    "\t\t\t\tremove=false\n" +
                    "\t\t\t\techo \"set add $file  $remove $add\"\n" +
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
                    "\tdone<\"$1\"\n" +
                    "}\n" +
                    "\n" +
                    "modifyProject \"${1-.}\"";
}
