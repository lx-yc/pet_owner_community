package com.example.exception;

import com.example.enums.ResultCode;
import com.example.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有未知异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("全局异常：", e);
        return Result.error(ResultCode.ERROR.getCode(),ResultCode.ERROR.getMsg());
    }

    /**
     * 捕获运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage());
        return Result.error(500,e.getMessage());
    }

    /**
     * 自定义业务异常（推荐使用！）
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);
        return Result.error("数据不存在或为空");
    }

    /**
     * SQL 语法/执行异常
     */
    @ExceptionHandler(java.sql.SQLException.class)
    public Result handleSQLException(java.sql.SQLException e) {
        log.error("数据库异常：", e);
        return Result.error("数据操作失败");
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public Result handleMissingPathVariableException(MissingPathVariableException e) {
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 打印请求路径和方法
        System.out.println("触发异常的请求路径：" + request.getRequestURI());
        System.out.println("请求方法：" + request.getMethod());
        System.out.println("缺失的路径变量：" + e.getVariableName());
        return Result.error("请求路径变量缺失：" + e.getVariableName());
    }
}
