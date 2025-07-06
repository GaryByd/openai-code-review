package com.luojiawei.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenAiCodeReview {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello OpenAI Code Review SDK!");

        // 1.代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while((line = reader.readLine()) != null) {
            diffCode.append(line);
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exited with code: " + exitCode);
        System.out.println("评审代码: " + diffCode.toString());
        // 2.调用OpenAI API进行代码评审
    }
}
