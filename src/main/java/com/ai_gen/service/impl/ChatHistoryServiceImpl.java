package com.ai_gen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.ai_gen.entity.ChatHistory;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.mapper.ChatHistoryMapper;
import com.ai_gen.model.dto.chatHistory.ChatHistoryAdminQueryRequest;
import com.ai_gen.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.ai_gen.model.vo.ChatHistoryCursorResponse;
import com.ai_gen.model.vo.ChatHistoryVO;
import com.ai_gen.service.ChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话历史 服务层实现。
 *
 * @author jimmy
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Override
    public boolean saveChatHistory(ChatHistory chatHistory) {
        if (ObjUtil.hasEmpty(chatHistory, chatHistory.getMessage(),
                chatHistory.getMessageType(), chatHistory.getAppId(), chatHistory.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对话历史参数不完整");
        }
        return this.save(chatHistory);
    }

    @Override
    public boolean removeByAppId(Long appId) {
        if (ObjUtil.isEmpty(appId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appId不能为空");
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId);
        // MyBatis-Flex 逻辑删除：生成 UPDATE SET isDelete=1 WHERE appId=? AND isDelete=0
        return this.remove(queryWrapper);
    }

    @Override
    public ChatHistoryCursorResponse listMyChatHistory(ChatHistoryQueryRequest queryRequest, Long loginUserId) {
        if (ObjUtil.isEmpty(queryRequest) || ObjUtil.isEmpty(queryRequest.getAppId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appId不能为空");
        }

        int pageSize = ObjUtil.isEmpty(queryRequest.getPageSize()) ? 10 : queryRequest.getPageSize();
        if (pageSize > 50) {
            pageSize = 50;
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", queryRequest.getAppId())
                .eq("userId", loginUserId);

        // 游标条件：查询早于 cursor 的消息
        if (queryRequest.getCursor() != null) {
            queryWrapper.lt("createTime", queryRequest.getCursor());
        }

        // 按创建时间降序（最新优先）
        queryWrapper.orderBy("createTime", false);
        // 多查一条用于判断 hasMore
        queryWrapper.limit(pageSize + 1);

        List<ChatHistory> records = this.list(queryWrapper);

        boolean hasMore = records.size() > pageSize;
        if (hasMore) {
            // 截断到 pageSize，多出的那条仅用于判断
            records = records.subList(0, pageSize);
        }

        LocalDateTime nextCursor = records.isEmpty() ? null : records.get(records.size() - 1).getCreateTime();

        List<ChatHistoryVO> voList = records.stream()
                .map(entity -> BeanUtil.copyProperties(entity, ChatHistoryVO.class))
                .collect(Collectors.toList());

        return ChatHistoryCursorResponse.builder()
                .records(voList)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public Page<ChatHistoryVO> adminListChatHistory(ChatHistoryAdminQueryRequest adminQueryRequest) {
        if (ObjUtil.isEmpty(adminQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        int pageNum = adminQueryRequest.getPageNum();
        int pageSize = adminQueryRequest.getPageSize();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", adminQueryRequest.getAppId())
                .eq("userId", adminQueryRequest.getUserId())
                .orderBy("createTime", false);

        Page<ChatHistory> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<ChatHistoryVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        voPage.setRecords(
                page.getRecords().stream()
                        .map(entity -> BeanUtil.copyProperties(entity, ChatHistoryVO.class))
                        .collect(Collectors.toList())
        );
        return voPage;
    }
}
