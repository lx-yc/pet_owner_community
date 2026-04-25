package com.example.utils;

import com.example.enums.ResultCode;
import com.example.exception.BusinessException;

/**
 * 全局通用校验工具类
 * 静态方法，全局通用
 */
public final class GlobalCheckUtil {

    // 私有构造器，防止实例化
    private GlobalCheckUtil() {}

    /**
     * 1. 校验登录状态
     * @return 当前登录用户ID
     */
    public static Long checkLogin() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.FAILED); // 建议换成 ResultCode.NOT_LOGIN 更语义化
        }
        return userId;
    }

    /**
     * 2. 通用对象非空校验
     * @param obj 被校验对象
     * @param code 异常码
     */
    public static void checkNotNull(Object obj, ResultCode code) {
        if (obj == null) {
            throw new BusinessException(code);
        }
    }

    /**
     * 3. 校验所有权（权限）
     * @param dataUserId 数据所属用户ID
     * @param loginUserId 当前登录用户ID
     */
    public static void checkOwner(Long dataUserId, Long loginUserId, ResultCode code) {
        if (!dataUserId.equals(loginUserId)) {
            throw new BusinessException(code);
        }
    }

    /**
     * 4. 校验数据库操作结果
     * @param rows 影响行数
     * @param code 异常码
     */
    public static void checkRowAffect(int rows, ResultCode code) {
        if (rows <= 0) {
            throw new BusinessException(code);
        }
    }
}
