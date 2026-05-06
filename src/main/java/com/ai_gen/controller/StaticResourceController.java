package com.ai_gen.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ai_gen.constants.AppConstant.CODE_OUTPUT_ROOT_DIR;

@RestController
@RequestMapping("/static")
public class StaticResourceController {

    // 1. 在类中添加属性
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    private static final String CODE_FILES_BASE_DIR = CODE_OUTPUT_ROOT_DIR;

    /**
     * 通用文件访问接口
     * 支持类似：/static/{deployKey}/index.html
     *          /static/{deployKey}/js/app.js
     *          /static/{deployKey}/css/style.css
     */
    @GetMapping("/{deployKey}/**")
    public ResponseEntity<Resource> serveFrontendFile(
            @PathVariable String deployKey,
            HttpServletRequest request) {

        try {
            // 获取请求的完整路径
            String requestPath = (String) request.getAttribute(
                    HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

            // 提取请求路径格式
            String pattern = (String) request.getAttribute(
                    HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

            // 使用AntPathMatcher提取文件名称
            String filePath = new AntPathMatcher().extractPathWithinPattern(pattern, requestPath);

            // 构建完整的文件系统路径
            Path fullPath = Paths.get(CODE_FILES_BASE_DIR, deployKey, filePath)
                    .normalize()
                    .toAbsolutePath();

            // 安全检查：确保路径在基础目录内
            Path basePath = Paths.get(CODE_FILES_BASE_DIR).toAbsolutePath();
            if (!fullPath.startsWith(basePath)) {
                return ResponseEntity.status(403).build();
            }

            // 检查文件是否存在
            if (!Files.exists(fullPath) || !Files.isRegularFile(fullPath)) {
                // 如果请求的是前端路由，返回index.html
                if (isFrontendRoute(filePath)) {
                    return serveIndexHtml(deployKey);
                }
                // 重定向到 index.html
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(contextPath+"/static/" + deployKey + "/index.html"))
                        .build();
            }

            // 创建Resource并返回
            File file = fullPath.toFile();
            Resource resource = new FileSystemResource(file);

            // 设置Content-Type
            String contentType = determineContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 默认首页
     */
    @GetMapping("/{deployKey}")
    public ResponseEntity<Resource> serveIndex(
            @PathVariable String deployKey) {
        return serveIndexHtml(deployKey);
    }

    /**
     * 返回index.html
     */
    private ResponseEntity<Resource> serveIndexHtml(String deployKey) {
        try {
            Path indexPath = Paths.get(CODE_FILES_BASE_DIR, deployKey, "index.html");

            if (!Files.exists(indexPath)) {
                return ResponseEntity.notFound().build();
            }

            File file = indexPath.toFile();
            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 判断是否为前端路由
     */
    private boolean isFrontendRoute(String filePath) {
        // 没有文件扩展名的路径，可能是前端路由
        return !filePath.contains(".") ||
                filePath.endsWith("/") ||
                // 或者检查是否是常见的前端路由模式
                filePath.matches(".*/[^./]+$");
    }

    /**
     * 根据文件扩展名确定Content-Type
     */
    private String determineContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();

        if (lowerPath.endsWith(".html")) return "text/html";
        if (lowerPath.endsWith(".htm")) return "text/html";
        if (lowerPath.endsWith(".js")) return "application/javascript";
        if (lowerPath.endsWith(".mjs")) return "application/javascript";
        if (lowerPath.endsWith(".css")) return "text/css";
        if (lowerPath.endsWith(".json")) return "application/json";
        if (lowerPath.endsWith(".png")) return "image/png";
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) return "image/jpeg";
        if (lowerPath.endsWith(".gif")) return "image/gif";
        if (lowerPath.endsWith(".svg")) return "image/svg+xml";
        if (lowerPath.endsWith(".ico")) return "image/x-icon";
        if (lowerPath.endsWith(".woff")) return "font/woff";
        if (lowerPath.endsWith(".woff2")) return "font/woff2";
        if (lowerPath.endsWith(".ttf")) return "font/ttf";
        if (lowerPath.endsWith(".eot")) return "application/vnd.ms-fontobject";
        if (lowerPath.endsWith(".otf")) return "font/otf";
        if (lowerPath.endsWith(".txt")) return "text/plain";
        if (lowerPath.endsWith(".xml")) return "application/xml";

        return "application/octet-stream";
    }
}