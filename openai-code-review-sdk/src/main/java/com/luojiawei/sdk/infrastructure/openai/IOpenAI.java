package com.luojiawei.sdk.infrastructure.openai;

import com.luojiawei.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.luojiawei.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

public interface IOpenAI {
    public ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;
}
