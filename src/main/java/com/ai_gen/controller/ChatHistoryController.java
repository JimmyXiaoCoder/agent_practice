package com.ai_gen.controller;

import cn.hutool.core.util.ObjUtil;
import com.ai_gen.annotation.AuthCheck;
import com.ai_gen.entity.User;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.model.dto.chatHistory.ChatHistoryAdminQueryRequest;
import com.ai_gen.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.ai_gen.model.vo.ChatHistoryCursorResponse;
import com.ai_gen.model.vo.ChatHistoryVO;
import com.ai_gen.response.BaseResponse;
import com.ai_gen.service.ChatHistoryService;
import com.ai_gen.service.UserService;
import com.ai_gen.utils.ResultUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ai_gen.constants.UserConstant.ADMIN_ROLE;

/**
 * 对话历史 控制层。
 *
 * @author jimmy
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private UserService userService;

    /**
     * 用户查询自己应用的对话记录（游标分页，最新优先）
     *
     * @param queryRequest 查询请求
     * @param request      HTTP 请求
     * @return 游标分页响应（含记录列表、下一页游标、是否有更多数据）
     */
    @PostMapping("/my/list")
    public BaseResponse<ChatHistoryCursorResponse> listMyChatHistory(
            @RequestBody ChatHistoryQueryRequest queryRequest,
            HttpServletRequest request) {
        if (ObjUtil.isEmpty(queryRequest) || ObjUtil.isEmpty(queryRequest.getAppId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appId不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        ChatHistoryCursorResponse response = chatHistoryService.listMyChatHistory(queryRequest, loginUser.getId());
        return ResultUtils.success(response);
    }

    /**
     * 管理员分页查询所有对话记录
     *
     * @param adminQueryRequest 管理员查询请求
     * @return 对话历史分页
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/admin/list/page")
    public BaseResponse<Page<ChatHistoryVO>> adminListChatHistory(
            @RequestBody ChatHistoryAdminQueryRequest adminQueryRequest) {
        Page<ChatHistoryVO> page = chatHistoryService.adminListChatHistory(adminQueryRequest);
        return ResultUtils.success(page);
    }
}
