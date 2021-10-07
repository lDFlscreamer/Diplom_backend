package org.diplom.diplom_backend.constant;

/**
 * contains all docker command template
 */
public class DockerCommandConstant {
    public static final String IMAGES = "docker images";
    public static final String CREATE_IMAGE = "docker build  -t {0}  -f {1} {2} ";
    public static final String REMOVE_EXTRA_CONTAINER = "docker container prune -f ";
    public static final String REMOVE_IMAGE = "docker rmi -f {0}";
    public static final String GET_LIST_OF_EXTRA_IMAGE = "docker images --filter dangling=true -q --no-trunc";
    public static final String GET_LIST_OF_IMAGE_BY_USER_LOGIN = "docker images -f label=user={0} -q --no-trunc";
    public static final String RUN_IMAGE = "docker run --rm -iP {0} {1}";
    public static final String GET_CONTAINER_ID_BY_IMAGENAME = "docker ps --filter ancestor={0} -q --no-trunc";
    public static final String STOP_CONTAINER = "docker kill {0}";
    public static final String GET_PORT_INFORMATION_BY_IMAGE_NAME =" docker port  {0}";

    //docker inspect $(docker ps --filter "ancestor=test" -q) | grep IPAddress
//docker images -f "label=user="root"" -q --no-trunc
    //docker ps --filter ancestor=$(docker images -f "label=user="root"" -q --no-trunc)

}
