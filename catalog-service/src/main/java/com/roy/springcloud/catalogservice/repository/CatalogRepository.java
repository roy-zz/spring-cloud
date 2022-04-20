package com.roy.springcloud.catalogservice.repository;

import com.roy.springcloud.catalogservice.domain.Catalog;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CatalogRepository extends CrudRepository<Catalog, Long> {
    Optional<Catalog> findByProductId(String productId);
}
