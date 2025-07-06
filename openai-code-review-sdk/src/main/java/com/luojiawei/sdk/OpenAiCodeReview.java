package com.luojiawei.sdk;

import com.alibaba.fastjson2.JSON;
import com.luojiawei.sdk.domain.model.ChatCompletionRequest;
import com.luojiawei.sdk.domain.model.ChatCompletionSyncResponse;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class OpenAiCodeReview {
    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
        System.out.println("Hello OpenAI Code Review SDK!");
        String token = System.getenv("GITHUB_TOKEN");
        if(null == token || token.isEmpty()){
            throw new RuntimeException("token is null");
        }

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

        // 3.将日志写入到github
        String logUrl = writeLog(token, log);
        System.out.println(logUrl);

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

    private static String writeLog(String token,String log) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI("https://github.com/GaryByd/openai-code-review-log.git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();

        String dataFolderName = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        File dataFolder = new File("repo/" + dataFolderName);
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + ".md";
        File newFile = new File(dataFolder, fileName);
        try(FileWriter writer = new FileWriter(newFile)) {
            writer.write(log);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        git.add().addFilepattern(dataFolderName + "/" + fileName).call();
        git.commit().setMessage("Add log file: " + fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();

        // 这里可以实现将日志写入到指定的文件或数据库中
        return "https://github.com/GaryByd/openai-code-review-log/blob/master/"+dataFolderName+ "/" + fileName;
    }
}
