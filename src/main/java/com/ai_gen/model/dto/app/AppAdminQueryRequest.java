package com.ai_gen.model.dto.app;

import com.ai_gen.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppAdminQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String appName;

    private String cover;

    private String initPrompt;

    private String codeGenType;

    private String deployKey;

    private Integer priority;

    private Long userId;

    private Integer isDelete;

    @Serial
    private static final long serialVersionUID = 1L;
}
