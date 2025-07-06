package com.luojiawei.sdk;

import com.luojiawei.sdk.domain.service.impl.OpenAiCodeReviewService;
import com.luojiawei.sdk.infrastructure.git.GitCommand;
import com.luojiawei.sdk.infrastructure.openai.IOpenAI;
import com.luojiawei.sdk.infrastructure.openai.impl.ChatGLM;
import com.luojiawei.sdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    // 配置配置
    private String weixin_appid = "wx7614eea931e4d374";
    private String weixin_secret = "da9a2e3c6412a99b82f196c14e453ca0";
    private String weixin_touser = "o7Rw_vq2r5YUtH2zsJlyqvty4aMA";
    private String weixin_template_id = "dJEtVyeZuMDzdstjzdv6sKylDnAE0awqN4ODJFNNgsI";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://api.ppinfra.com/v3/openai/chat/completions";
    private String chatglm_apiKeySecret = "sk_aDjk0JmtHCg9FZgiURtgKSKy9cxz3WD9OVflhg43-qQ";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static String default_model;

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /*
         * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
         */
        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );
        IOpenAI openAI = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));
        // 设置默认模型
        OpenAiCodeReview.default_model = getEnv("DEFAULT_MODEL");
        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, weiXin);
        openAiCodeReviewService.exec();
        logger.info("openai-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

}
