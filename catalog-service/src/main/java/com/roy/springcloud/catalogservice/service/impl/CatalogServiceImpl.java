package com.roy.springcloud.catalogservice.service.impl;

import com.roy.springcloud.catalogservice.domain.Catalog;
import com.roy.springcloud.catalogservice.dto.CatalogDto;
import com.roy.springcloud.catalogservice.repository.CatalogRepository;
import com.roy.springcloud.catalogservice.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.roy.springcloud.catalogservice.mapper.MapperUtil.toObject;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final Environment environment;
    private final CatalogRepository catalogRepository;

    @Override
    public List<CatalogDto> getAllCatalogs() {
        Iterable<Catalog> savedCatalogs = catalogRepository.findAll();
        List<CatalogDto> response = new ArrayList<>();
        savedCatalogs.forEach(catalog -> {
            response.add(toObject(catalog, CatalogDto.class));
        });
        return response;
    }

}
