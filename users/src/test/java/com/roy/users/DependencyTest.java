package com.roy.users;

import com.roy.domain.DomainModuleClazz;
import com.roy.util.UtilModuleClazz;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyTest {
    @Test
    @DisplayName("멀티 모듈 정상작동 테스트")
    void multiModuleTest() {
        assertDoesNotThrow(() -> {
            DomainModuleClazz domainModuleClazz = new DomainModuleClazz(1L);
            UtilModuleClazz utilModuleClazz = new UtilModuleClazz(1L);
            assertNotNull(domainModuleClazz);
            assertNotNull(utilModuleClazz);
        });
    }
}
