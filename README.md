# OpenAI Code Review SDK

## 项目简介

OpenAI Code Review SDK 是一个基于大模型（如 OpenAI/ChatGLM）和微信消息推送的自动化代码审查工具。它可集成于 CI/CD 流程，自动对 Git 仓库的代码变更进行智能审查、生成优化建议，并通过微信模板消息推送审查结果。

## 主要特性
- 支持 Git 代码变更自动化审查（基于 diff）
- 集成 OpenAI/ChatGLM 等大模型接口，自动生成专业代码评审建议
- 支持微信模板消息推送审查结果
- 可自定义模型、API 地址、消息模板等参数
- 适用于多分支、多项目、多作者场景

## 目录结构
```
openai-code-review-sdk/    # SDK主模块
openai-code-review-test/   # 测试与示例
src/                       # 入口与主程序
README.md                  # 项目说明
.github/workflows/         # GitHub Actions 工作流
```

## 快速开始

### 1. 克隆项目
```bash
git clone <本仓库地址>
cd openai-code-review
```

### 2. 配置环境变量
本项目通过系统环境变量进行配置，主要包括：

#### GitHub 相关
- `GITHUB_REVIEW_LOG_URI` 代码审查日志地址（如：https://github.com/xfg-studio-project/openai-code-review-log）
- `GITHUB_TOKEN` GitHub 访问令牌
- `COMMIT_PROJECT` 项目名（自动获取）
- `COMMIT_BRANCH` 分支名（自动获取）
- `COMMIT_AUTHOR` 提交作者（自动获取）
- `COMMIT_MESSAGE` 提交说明（自动获取）

#### OpenAI/ChatGLM 相关
- `CHATGLM_APIHOST` ChatGLM API 地址（如：https://open.bigmodel.cn/api/paas/v4/chat/completions）
- `CHATGLM_APIKEYSECRET` ChatGLM API 密钥
- `DEFAULT_MODEL` 使用的模型名称（如 gpt-3.5-turbo、chatglm-6b 等）

#### 微信公众号相关
- `WEIXIN_APPID` 微信公众号 AppID
- `WEIXIN_SECRET` 微信公众号 AppSecret
- `WEIXIN_TOUSER` 微信消息接收人 openid
- `WEIXIN_TEMPLATE_ID` 微信模板消息ID

> 建议将敏感信息配置在 GitHub Actions 的 secrets 中。

### 3. 构建项目
```bash
mvn clean package
```

### 4. 运行
```bash
java -jar openai-code-review-sdk/target/openai-code-review-sdk-1.0-shaded.jar
```
或参考 `.github/workflows/main-maven-jar.yml` 集成到 GitHub Actions。

## 主要类说明
- `OpenAiCodeReview.java`  主程序入口，负责环境变量读取与流程调度
- `OpenAiCodeReviewService.java`  代码审查主逻辑
- `ChatGLM.java`           大模型 API 封装
- `GitCommand.java`        Git 操作封装
- `WeiXin.java`            微信消息推送封装

## 工作流程
1. 读取环境变量，初始化 Git、AI、微信等服务
2. 获取本次提交的 diff 代码
3. 调用大模型接口生成代码审查建议
4. 自动提交审查建议到代码仓库
5. 通过微信模板消息推送审查结果

## GitHub Actions 示例
详见 `.github/workflows/main-maven-jar.yml`，支持自动获取分支、作者、提交信息，并安全注入各类密钥。

## 依赖环境
- JDK 8 及以上
- Maven 3.x

## 贡献
欢迎提交 issue 和 PR 参与项目共建。

## License
MIT

