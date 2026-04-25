package com.example.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象转换工具类
 * 解决：DTO → Entity、Entity → VO 批量复制问题
 */
public class BeanConvertUtils {

    /**
     * 单个对象转换
     * 例如：Post post = BeanConvertUtils.convert(dto, Post.class);
     */
    public static <S, T> T convert(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象转换异常", e);
        }
    }

    /**
     * 集合转换
     * 例如：List<PostVO> voList = BeanConvertUtils.convertList(postList, PostVO.class);
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return null;
        }
        return sourceList.stream()
                .map(source -> convert(source, targetClass))
                .collect(Collectors.toList());
    }
}
