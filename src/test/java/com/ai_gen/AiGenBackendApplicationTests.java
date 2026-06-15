package com.ai_gen;

import com.ai_gen.core.AiCodeGeneratorFacade;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;
import com.ai_gen.service.AiCodeGeneratorService;
import com.ai_gen.utils.CodeFileSaver;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class AiGenBackendApplicationTests {

    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

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

    @Test
    void codeStreamGenTest() {

        Flux<String> stringFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream("写一个todoList，代码不超过200行", CodeGenTypeEnum.HTML,406954422623186944L);

        List<String> result = stringFlux.collectList().block();
        Assertions.assertNotNull(result);
        String join = String.join("", result);
        Assertions.assertNotNull(join);
        System.out.println(join);
    }

}
