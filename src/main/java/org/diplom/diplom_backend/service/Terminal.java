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
    List<String> runCommandAndGetResult(String command) {
        List<String> stdout = null;
        List<String> err ;
        try {
            Process p = this.runCommand(command);

            stdout = new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines().collect(Collectors.toList());
            err = new BufferedReader(new InputStreamReader(p.getErrorStream()))
                    .lines().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            err= new ArrayList<>();
            err.add(e.getMessage());
        }

        return  err.isEmpty() ? stdout : err;
    }
}
