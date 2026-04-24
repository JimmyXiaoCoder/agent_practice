package com.ai_gen.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class MultiFileCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("CSS代码")
    private String cssCode;

    @Description("javascript代码")
    private String jsCode;

    @Description("生成的代码描述")
    private String description;
}
