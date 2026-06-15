package com.ai_gen.model.dto.chatHistory;

import com.ai_gen.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员对话历史分页查询请求
 *
 * @author jimmy
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryAdminQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用 id（可选筛选）
     */
    private Long appId;

    /**
     * 用户 id（可选筛选）
     */
    private Long userId;
}
