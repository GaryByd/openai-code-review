package com.luojiawei.sdk.domain.service;

import com.luojiawei.sdk.infrastructure.git.GitCommand;
import com.luojiawei.sdk.infrastructure.openai.IOpenAI;
import com.luojiawei.sdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public abstract class AbstractOpenAiCodeReviewService implements IOpenAiCodeReviewService {
    private final Logger logger = (Logger) LoggerFactory.getLogger(AbstractOpenAiCodeReviewService.class);

    protected final GitCommand gitCommand;

    protected final IOpenAI openAI;

    protected final WeiXin weiXin;

    protected AbstractOpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weiXin = weiXin;
    }
    @Override
    public void exec() {
        try {
            // 1. 获取代码变更
            String diffCode = getDiffCode();
            // 2. 调用 OpenAI 接口进行代码审查
            String recommend = codeReview(diffCode);
            // 3. 处理 OpenAI 返回的结果 返回日志地址
            String logUrl = recordCodeReview(recommend);
            // 4. 发送结果到微信
            pushMessage(logUrl);
            logger.info("Executing code review service...");
        } catch (Exception e) {
//           logger.error("Error during code review execution: {}", e.getMessage(), e);
            throw new RuntimeException("Error during code review execution", e);
        }
    }

    protected abstract String  getDiffCode() throws IOException, InterruptedException;
    protected abstract String codeReview(String diffCode) throws Exception;
    protected abstract void pushMessage(String logUrl) throws Exception;
    protected abstract String recordCodeReview(String diffCode) throws Exception;

}
