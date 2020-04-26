package org.diplom.diplom_backend.constant;

public class CommandConstant {
    public static final String IMAGES = "docker images";
    public static final String CREATE_IMAGE = "docker build  -t {0}  -f {1} {2}";
    public static final String RUN_IMAGE = "docker run   {0} {1}";


}
