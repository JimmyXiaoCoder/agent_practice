package com.ai_gen.service;

import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/html-prompt.txt")
    HtmlCodeResult generateHTMLCode(String userMessage);

    @SystemMessage(fromResource = "prompt/multi-file-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);

    /**
     * 生成 HTML 代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/html-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 生成多文件代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/multi-file-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);

}
