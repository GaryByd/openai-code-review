package com.luojiawei.sdk;

import com.alibaba.fastjson2.JSON;
import com.luojiawei.sdk.domain.model.ChatCompletionRequest;
import com.luojiawei.sdk.domain.model.ChatCompletionSyncResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
        System.out.println("diff code: " + diffCode.toString());
        // 2.调用chatglm
        String log = codeReview(diffCode.toString());

        System.out.println("code codeReview"+log);

    }
    public static String codeReview(String code) throws IOException {
        String apiKeySecret = "sk_aDjk0JmtHCg9FZgiURtgKSKy9cxz3WD9OVflhg43-qQ";

        URL url = new URL("https://api.ppinfra.com/v3/openai/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKeySecret);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        // 设置连接超时和读取超时
        connection.setDoOutput(true);
//        String jsonInpuString = "{"
//                + "\"model\":\"deepseek/deepseek-v3-0324\","
//                + "\"messages\": ["
//                + "    {"
//                + "        \"role\": \"user\","
//                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: " + code + "\""
//                + "    }"
//                + "]"
//                + "}";

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel("deepseek/deepseek-v3-0324");

        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>(){{
            add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: "));
            add(new ChatCompletionRequest.Prompt("user", code));
        }});
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes("utf-8");
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();

        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        System.out.println(content.toString());

        ChatCompletionSyncResponse chatCompletionSyncResponse = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
        return chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent();
    }
}
