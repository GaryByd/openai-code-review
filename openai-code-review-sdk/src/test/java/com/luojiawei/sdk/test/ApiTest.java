package com.luojiawei.sdk.test;

import com.alibaba.fastjson2.JSON;
import com.luojiawei.sdk.domain.model.ChatCompletionSyncResponse;
import com.luojiawei.sdk.domain.model.Message;
import com.luojiawei.sdk.types.utils.BearerTokenUtils;
import com.luojiawei.sdk.types.utils.WXAccessTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@RunWith(SpringRunner.class)
public class ApiTest {
    public static void main(String[] args) {
//        String apiKeySecret = "sk_aDjk0JmtHCg9FZgiURtgKSKy9cxz3WD9OVflhg43-qQ";
//        String token = BearerTokenUtils.getToken(apiKeySecret);
//        System.out.println(token);

    }

    @Test
    public void test_http() throws IOException {
        String apiKeySecret = "sk_aDjk0JmtHCg9FZgiURtgKSKy9cxz3WD9OVflhg43-qQ";

        URL url = new URL("https://api.ppinfra.com/v3/openai/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKeySecret);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        // 设置连接超时和读取超时
        connection.setDoOutput(true);
        String code = "1+1";
        String jsonInpuString = "{"
                + "\"model\":\"deepseek/deepseek-v3-0324\","
                + "\"messages\": ["
                + "    {"
                + "        \"role\": \"user\","
                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: " + code + "\""
                + "    }"
                + "]"
                + "}";

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInpuString.getBytes("utf-8");
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
        System.out.println(chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent());
    }

    @Test
    public void test_wx(){
        String accessToken = WXAccessTokenUtils.getAccessToken();
        Message message = new Message();
        message.put("project","big-market");
        message.put("review","feat: 新加功能");
        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);
        sendPostRequest(url,JSON.toJSONString(message));
    }

    private static void sendPostRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
