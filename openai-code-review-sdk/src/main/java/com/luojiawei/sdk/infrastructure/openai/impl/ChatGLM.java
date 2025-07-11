package com.luojiawei.sdk.infrastructure.openai.impl;

import com.alibaba.fastjson2.JSON;
import com.luojiawei.sdk.infrastructure.openai.IOpenAI;
import com.luojiawei.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.luojiawei.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatGLM implements IOpenAI {

    private final String apiHost;
    private final String apiKeySecret;

    public ChatGLM(String apiHost, String apiKeySecret) {
        this.apiHost = apiHost;
        this.apiKeySecret = apiKeySecret;
    }

    @Override
    public ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception {
        URL url = new URL(apiHost);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKeySecret);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(requestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.err.println("[ChatGLM] 请求失败，URL: " + apiHost + ", 响应码: " + responseCode);
                System.err.println("响应信息: " + connection.getResponseMessage());
                throw new RuntimeException("[ChatGLM] 请求失败，响应码: " + responseCode + ", 响应信息: " + connection.getResponseMessage());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);
        } catch (FileNotFoundException e) {
            System.err.println("[ChatGLM] FileNotFoundException，URL: " + apiHost);
            e.printStackTrace();
            throw e;
        }
    }

}
