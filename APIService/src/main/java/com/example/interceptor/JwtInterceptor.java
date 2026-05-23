package com.example.interceptor;

import com.example.entity.User;
import com.example.utils.JwtUtil;
import com.example.utils.Result;
import com.example.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_TOKEN_KEY = "login:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行 OPTIONS 请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        boolean isPostListApi = requestUri.equals("/post/list"); // 只允许这个接口游客访问
        boolean isPostDetailApi = requestUri.startsWith("/post/detail");
        boolean isCommentPageTopApi = requestUri.startsWith("/comment/page/top");
        boolean isCommentPageReplyApi = requestUri.startsWith("/comment/page/reply");
        boolean isUserDetailApi = requestUri.startsWith("/user/detail");
        boolean isPostUserApi = requestUri.startsWith("/post/user");

        String token = request.getHeader("token");

        // 如果是【帖子列表接口】
        if (isPostListApi || isPostDetailApi || isCommentPageTopApi || isCommentPageReplyApi || isUserDetailApi || isPostUserApi) {
            // 有 token 才尝试解析，没有直接放行
            if (token != null && !token.isEmpty()) {
                try {
                    // 必须同时校验 Redis + JWT（和登录逻辑完全一致）
                    String redisUserIdStr = (String) redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
                    if (redisUserIdStr == null) {
                        UserContext.clear();
//                        return true; // token过期 → 不报错，当游客
                        return writeError(response, "redis异常，请重新登录");
                    }

                    Long redisUserId = Long.parseLong(redisUserIdStr);
                    Map<String, Object> claims = JwtUtil.parseToken(token);
                    Long jwtUserId = Long.parseLong(claims.get("id").toString());

                    if (redisUserId.equals(jwtUserId)) {
                        UserContext.setUserId(redisUserId); // 校验成功 → 存userId
                    }
                } catch (Exception e) {
                    UserContext.clear(); // 出错 → 清空，当游客
                    return writeError(response, "异常，请重新登录！");
                }
            }
            // 列表接口：无论如何都放行！
            return true;
        }

        if (token == null || token.isEmpty()) {
            return writeError(response, "未登录");
        }
        String redisUserIdStr = (String) redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
        Long redisUserId = null;
        if (redisUserIdStr != null) {
            redisUserId = Long.parseLong(redisUserIdStr);
        }
        if (redisUserId == null) {
            return writeError(response, "未登录");
        }

        //JWT校验token是否过期
        Long jwtUserId;
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            jwtUserId = Long.parseLong(claims.get("id").toString());
        } catch (Exception e) {
            String msg = e.getMessage();
            if ("token已过期".equals(msg)) {
                return writeError(response, "登录已过期，请重新登录！");
            } else {
                return writeError(response, "token无效");
            }
        }

        if(!redisUserId.equals(jwtUserId)){
            return writeError(response, "身份异常，请重新登录");
        }
        //             存入ThreadLocal
        UserContext.setUserId(redisUserId);
        return true;
    }

    private boolean writeError(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 加这一行！

        Result<Void> result = Result.error(msg);

        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
