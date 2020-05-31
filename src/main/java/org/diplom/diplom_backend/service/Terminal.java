package org.diplom.diplom_backend.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class contain 3 methods
 *
 * @author Tverdokhlib
 */
@Service
public class Terminal {
    Runtime runtime = Runtime.getRuntime();

    /**
     * Run terminal command
     *
     * @param command command that will be executed
     * @return instance of {@link Process}
     * @throws IOException throws if command can`t be executed
     * @see Process
     */
    public Process runCommand(String command) throws IOException {
        return runtime.exec(command);
    }

    /**
     * Get Output from Process
     * @param process process instance
     * @return list of outputted string
     */
    public List<String> getOutputFromProcess(Process process) {
        List<String> stdout;
        stdout = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines().collect(Collectors.toList());
        return stdout;
    }

    /**
     * Get error from Process
     * @param process process instance
     * @return list of error string ,return empty list if everything is fine
     */
    public List<String> getErrorFromProcess(Process process) {
        List<String> stderr;
        stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                .lines().collect(Collectors.toList());
        return stderr;
    }


}
