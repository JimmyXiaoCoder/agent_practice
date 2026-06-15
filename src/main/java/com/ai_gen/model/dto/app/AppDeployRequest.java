package com.ai_gen.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppDeployRequest implements Serializable {

    private Long id;

    private String deployKey;

    private static final long serialVersionUID = 1L;

}
