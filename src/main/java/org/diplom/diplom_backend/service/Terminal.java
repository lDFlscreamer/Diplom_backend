package org.diplom.diplom_backend.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Terminal {
    Runtime runtime = Runtime.getRuntime();

    Process runCommand(String command) throws IOException {
        return runtime.exec(command);
    }

    List<String> getOutputFromProcess(Process p) {
        List<String> stdout = null;
        List<String> err = new ArrayList<>();

        stdout = new BufferedReader(new InputStreamReader(p.getInputStream()))
                .lines().collect(Collectors.toList());
        err.addAll(new BufferedReader(new InputStreamReader(p.getErrorStream()))
                .lines().collect(Collectors.toList()));


        if (!err.isEmpty()) {
            stdout.addAll(err);
        }
        return stdout;
    }
}
