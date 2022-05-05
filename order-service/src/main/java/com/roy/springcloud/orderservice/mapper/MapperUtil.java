package com.roy.springcloud.orderservice.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapperUtil {
    public static <T, S> T toObject(S source, Class<T> targetClass, String... ignoreProperties) {
        T target = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public static <T, S> List<T> toList(List<S> sources, Class<T> targetClass, String... ignoreProperties) {
        return sources.stream()
                .map(source -> toObject(source, targetClass, ignoreProperties))
                .collect(Collectors.toList());
    }
}

