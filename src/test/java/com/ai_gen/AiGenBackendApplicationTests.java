package com.ai_gen;

import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;
import com.ai_gen.service.AiCodeGeneratorService;
import com.ai_gen.utils.CodeFileSaver;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiGenBackendApplicationTests {

    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void contextLoads() {
    }

    @Test
    void codeGenTest() {

        HtmlCodeResult codeRes = aiCodeGeneratorService.generateHTMLCode("写一个todolist，代码不超过100行");
        CodeFileSaver.HTMLCodeGen(codeRes);
    }

    @Test
    void multiFileCodeGenTest() {

        MultiFileCodeResult code = aiCodeGeneratorService.generateMultiFileCode("写一个todolist，代码不超过100行");
        CodeFileSaver.MultiFileCodeGen(code);
    }

}
