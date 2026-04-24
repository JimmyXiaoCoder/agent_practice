package com.ai_gen.service;

import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/html-prompt.txt")
    HtmlCodeResult generateHTMLCode(String userMessage);

    @SystemMessage(fromResource = "prompt/multi-file-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
}
