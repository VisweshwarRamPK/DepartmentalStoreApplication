package com.viswa.DepartmentalStoreApplication.controller;


import com.viswa.DepartmentalStoreApplication.dto.ProductDto;
import com.viswa.DepartmentalStoreApplication.model.Product;
import com.viswa.DepartmentalStoreApplication.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department-store")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/add-product")
    public Product addProduct(@RequestBody ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update-product/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/delete-product/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/get-product/{id}")
    public Product getProduct(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/get-all-products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/buy-product/{id}")
    public ResponseEntity<String> buyProduct(@PathVariable String id, @RequestParam int quantity) {
        try {
            productService.buyProduct(id, quantity);
            return ResponseEntity.ok("Product purchased successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/cancel-product/{id}")
    public ResponseEntity<String> cancelProduct(@PathVariable String id, @RequestParam int quantity) {
        try {
            productService.cancelProduct(id, quantity);
            return ResponseEntity.ok("Product purchase canceled and stock updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
