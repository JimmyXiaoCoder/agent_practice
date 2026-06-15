package com.ai_gen.core;

import cn.hutool.core.util.StrUtil;
import com.ai_gen.core.parser.CodeParserExecutor;
import com.ai_gen.core.saver.CodeFileSaverExecutor;
import com.ai_gen.entity.ChatHistory;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.model.HtmlCodeResult;
import com.ai_gen.model.MultiFileCodeResult;
import com.ai_gen.service.AiCodeGeneratorService;
import com.ai_gen.service.ChatHistoryService;
import com.ai_gen.utils.CodeFileSaver;
import com.ai_gen.utils.CodeParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码 - 流式生成
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 id
     * @param userId          用户 id
     * @return 流式代码数据
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId, Long userId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }

        // 同步保存用户消息，确保即使 AI 调用失败用户消息也已落库
        ChatHistory userChat = ChatHistory.builder()
                .message(userMessage)
                .messageType("user")
                .appId(appId)
                .userId(userId)
                .build();
        try {
            chatHistoryService.saveChatHistory(userChat);
        } catch (Exception e) {
            log.error("保存用户对话历史失败: {}", e.getMessage());
        }

        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(stringFlux, codeGenTypeEnum, appId, userId);
            }
            case MULTI_FILE -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(stringFlux, codeGenTypeEnum, appId, userId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHTMLCode(userMessage);
        return CodeFileSaver.HTMLCodeGen(result);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.MultiFileCodeGen(result);
    }

    /**
     * 生成 HTML 模式的代码并保存 - 流式
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuilder sb = new StringBuilder();
        return stringFlux.doOnNext(chunk -> {
            sb.append(chunk);
        }).doOnComplete(() -> {
            try {
                String genCode = sb.toString();
                HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(genCode);
                File file = CodeFileSaver.HTMLCodeGen(htmlCodeResult);
                log.info("代码文件生成成功，路径为:"+file.getAbsolutePath());
            }
            catch (Exception e) {
                log.error("代码文件生成失败: {}",e.getMessage());
            }

        });
    }

    /**
     * 统一流处理
     *
     * @param codeStream      流式代码数据
     * @param codeGenTypeEnum 代码生成类型
     * @param appId           应用 id
     * @param userId          用户 id
     * @return 流式代码数据
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId, Long userId) {
        StringBuilder sb = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            sb.append(chunk);
        }).doOnComplete(() -> {
            // 保存 AI 完整响应
            String aiMessage = sb.toString();
            ChatHistory aiChat = ChatHistory.builder()
                    .message(aiMessage)
                    .messageType("ai")
                    .appId(appId)
                    .userId(userId)
                    .build();
            try {
                chatHistoryService.saveChatHistory(aiChat);
            } catch (Exception e) {
                log.error("保存AI对话历史失败: {}", e.getMessage());
            }
            // 原有代码解析保存逻辑
            try {
                Object codeResult = CodeParserExecutor.codeParse(aiMessage, codeGenTypeEnum);
                File file = CodeFileSaverExecutor.codeFileSave(codeResult, codeGenTypeEnum, appId);
                log.info("代码文件生成成功，路径为:" + file.getAbsolutePath());
            } catch (Exception e) {
                log.error("代码文件生成失败: {}", e.getMessage());
            }
        }).doOnError(error -> {
            // 保存 AI 错误/部分响应
            String partialMessage = sb.toString();
            String errorMessage = StrUtil.isBlank(partialMessage)
                    ? "[AI响应失败] " + error.getMessage()
                    : partialMessage + "\n[AI响应失败] " + error.getMessage();
            ChatHistory aiChat = ChatHistory.builder()
                    .message(errorMessage)
                    .messageType("ai")
                    .appId(appId)
                    .userId(userId)
                    .build();
            try {
                chatHistoryService.saveChatHistory(aiChat);
            } catch (Exception e) {
                log.error("保存AI对话历史(错误)失败: {}", e.getMessage());
            }
            log.error("AI流式生成失败: {}", error.getMessage());
        });
    }

    /**
     * 生成多文件模式的代码并保存 - 流式
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuilder sb = new StringBuilder();
        return stringFlux.doOnNext(chunk -> {
            sb.append(chunk);
        }).doOnComplete(() -> {
            try {
                String genCode = sb.toString();
                MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(genCode);
                File file = CodeFileSaver.MultiFileCodeGen(multiFileCodeResult);
                log.info("代码文件生成成功，路径为:"+file.getAbsolutePath());
            }
            catch (Exception e) {
                log.error("代码文件生成失败: {}",e.getMessage());
            }

        });
    }
}
