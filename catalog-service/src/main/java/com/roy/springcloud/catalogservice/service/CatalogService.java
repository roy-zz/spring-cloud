package com.roy.springcloud.catalogservice.service;

import com.roy.springcloud.catalogservice.dto.CatalogDto;

import java.util.List;

public interface CatalogService {
    List<CatalogDto> getAllCatalogs();
}
