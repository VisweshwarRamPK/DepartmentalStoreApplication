package com.viswa.DepartmentalStoreApplication.repository;

import com.viswa.DepartmentalStoreApplication.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepo extends MongoRepository<Product,String> {

    Product findByProductName(String productName);
}
