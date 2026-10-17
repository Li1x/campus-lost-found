package com.campus.lostfound.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.common.ResultCode;
import com.campus.lostfound.common.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 认证拦截器
 * 拦截请求，校验 Token 有效性和角色权限
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 非 Controller 方法（如静态资源）直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 从请求头中获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, ResultCode.UNAUTHORIZED, "请先登录");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            // 校验 Token
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String type = jwtUtil.getTypeFromToken(token);

            // 只接受 access token
            if (!"access".equals(type)) {
                sendError(response, ResultCode.UNAUTHORIZED, "无效的Token类型");
                return false;
            }

            // 设置用户上下文
            UserContext.setCurrentUser(userId, role);

            // 校验角色权限
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                // 检查类级别注解
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }

            if (requireRole != null) {
                UserRole currentRole = UserRole.fromCode(role);
                boolean hasPermission = Arrays.asList(requireRole.value()).contains(currentRole);
                if (!hasPermission) {
                    sendError(response, ResultCode.FORBIDDEN, "无权限访问");
                    return false;
                }
            }

            return true;
        } catch (JWTVerificationException e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            sendError(response, ResultCode.UNAUTHORIZED, "Token已过期或无效，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清除用户上下文，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 向客户端发送错误响应
     */
    private void sendError(HttpServletResponse response, ResultCode resultCode, String message) throws IOException {
        response.setStatus(resultCode.getCode());
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(resultCode, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
