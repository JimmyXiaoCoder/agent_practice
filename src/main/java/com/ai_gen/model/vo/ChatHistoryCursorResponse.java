package com.ai_gen.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史游标分页响应
 *
 * @author jimmy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryCursorResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对话记录列表
     */
    private List<ChatHistoryVO> records;

    /**
     * 下一页游标（本页最旧记录的 createTime），无更多数据时为 null
     */
    private LocalDateTime nextCursor;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;
}
