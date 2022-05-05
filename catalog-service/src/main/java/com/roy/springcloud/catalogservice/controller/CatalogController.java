package com.roy.springcloud.catalogservice.controller;

import com.roy.springcloud.catalogservice.dto.CatalogDto;
import com.roy.springcloud.catalogservice.service.CatalogService;
import com.roy.springcloud.catalogservice.vo.response.CatalogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.roy.springcloud.catalogservice.mapper.MapperUtil.toObject;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog-service")
public class CatalogController {
    private final Environment environment;
    private final CatalogService catalogService;

    @GetMapping("/health-check")
    public String healthCheck(HttpServletRequest request) {
        return String.format("It's working in catalog service on Port: %s", request.getServerPort());
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogResponse>> getCatalogs() {
        List<CatalogDto> savedCatalogs = catalogService.getAllCatalogs();
        List<CatalogResponse> response = savedCatalogs.stream()
                .map(catalog -> toObject(catalog, CatalogResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
