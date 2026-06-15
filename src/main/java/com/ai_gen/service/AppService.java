package com.ai_gen.service;

import com.ai_gen.entity.App;
import com.ai_gen.entity.User;
import com.ai_gen.model.dto.app.*;
import com.ai_gen.model.vo.AppVO;
import com.ai_gen.response.BaseResponse;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author jimmy
 */
public interface AppService extends IService<App> {

    Long addApp(AppAddRequest appAddRequest, User loginUser);

    BaseResponse<Boolean> updateMyApp(AppUpdateMyRequest appUpdateMyRequest, User loginUser);

    boolean removeMyApp(Long id, User loginUser);

    AppVO getMyAppById(Long id, User loginUser);

    Page<AppVO> listMyAppByPage(AppQueryRequest appQueryRequest, User loginUser);

    Page<AppVO> listGoodAppByPage(AppQueryRequest appQueryRequest);

    boolean adminRemoveApp(Long id);

    boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest);

    App adminGetAppById(Long id);

    Page<AppVO> adminListAppByPage(AppQueryRequest appQueryRequest);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Flux<String> chatToGenCode(String message, Long appId, User loginUser);

    BaseResponse<String> deployMyApp(AppDeployRequest appDeployRequest, User loginUser);
}
