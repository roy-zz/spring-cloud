package com.roy.springcloud.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

// @Configuration
public class FilterConfig {
    // @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/test-server-1/**")
                            .filters(f -> f.addRequestHeader("test-server-1-request", "test-server-1-request-header")
                                            .addResponseHeader("test-server-1-response", "test-server-1-response-header"))
                        .uri("lb://TEST-SERVER-1"))
                .route(r -> r.path("/test-server-2/**")
                            .filters(f -> f.addRequestHeader("test-server-2-request", "test-server-2-request-header")
                                            .addResponseHeader("test-server-2-response", "test-server-2-response-header"))
                        .uri("lb://TEST-SERVER-2"))
                .build();
    }
}
