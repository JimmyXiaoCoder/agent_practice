package com.ai_gen.service;

import com.ai_gen.entity.ChatHistory;
import com.ai_gen.model.dto.chatHistory.ChatHistoryAdminQueryRequest;
import com.ai_gen.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.ai_gen.model.vo.ChatHistoryCursorResponse;
import com.ai_gen.model.vo.ChatHistoryVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 对话历史 服务层。
 *
 * @author jimmy
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 保存对话历史记录（含参数校验）
     *
     * @param chatHistory 对话历史
     * @return 是否保存成功
     */
    boolean saveChatHistory(ChatHistory chatHistory);

    /**
     * 根据 appId 逻辑删除对话记录（级联删除用）
     *
     * @param appId 应用 id
     * @return 是否删除成功
     */
    boolean removeByAppId(Long appId);

    /**
     * 用户查询自己应用的对话记录（游标分页，最新优先）
     *
     * @param queryRequest 查询请求
     * @param loginUserId  当前登录用户 id
     * @return 游标分页响应（含记录列表、下一页游标、是否有更多数据）
     */
    ChatHistoryCursorResponse listMyChatHistory(ChatHistoryQueryRequest queryRequest, Long loginUserId);

    /**
     * 管理员分页查询所有对话记录
     *
     * @param adminQueryRequest 管理员查询请求
     * @return 对话历史分页
     */
    Page<ChatHistoryVO> adminListChatHistory(ChatHistoryAdminQueryRequest adminQueryRequest);
}
