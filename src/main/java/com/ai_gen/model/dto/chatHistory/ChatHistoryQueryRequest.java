package com.ai_gen.model.dto.chatHistory;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史游标分页查询请求
 *
 * @author jimmy
 */
@Data
public class ChatHistoryQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用 id（必填）
     */
    private Long appId;

    /**
     * 游标：上一页最早消息的 createTime，首次查询不传
     */
    private LocalDateTime cursor;

    /**
     * 每页条数，默认 10
     */
    private Integer pageSize = 10;
}
