package com.viswa.DepartmentalStoreApplication.service;


import com.viswa.DepartmentalStoreApplication.dto.ProductDto;
import com.viswa.DepartmentalStoreApplication.mapper.ProductMapper;
import com.viswa.DepartmentalStoreApplication.model.Billing;
import com.viswa.DepartmentalStoreApplication.model.Customerdetails;
import com.viswa.DepartmentalStoreApplication.model.Product;
import com.viswa.DepartmentalStoreApplication.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

public class ProductService {

    @Qualifier("departmentTemplate")
    @Autowired
    private MongoTemplate departmentTemplate;

    @Autowired
    private EmailService emailService;
    @Autowired
    private BillingService billingService;
    @Autowired
    private CustomerService customerService;

    public Product addProduct(ProductDto productDto) {
        Product product = mapDtoToProduct(productDto);
        return departmentTemplate.save(product);
    }

    public Product updateProduct(String id, ProductDto productDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Product existingProduct = departmentTemplate.findOne(query, Product.class);

        if (existingProduct != null) {
            existingProduct.setProductName(productDto.getProductName());
            existingProduct.setProductType(productDto.getProductType());
            existingProduct.setStocks(productDto.getStocks());
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setMrp(productDto.getMrp());
            existingProduct.setStoreName(productDto.getStoreName());
            departmentTemplate.save(existingProduct);
            return existingProduct;
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    public void deleteProduct(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        departmentTemplate.remove(query, Product.class);
    }

    public Product getProduct(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return departmentTemplate.findOne(query, Product.class);
    }

    public List<Product> getAllProducts() {
        return departmentTemplate.findAll(Product.class);
    }


    private Product mapDtoToProduct(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setProductName(productDto.getProductName());
        product.setProductType(productDto.getProductType());
        product.setStocks(productDto.getStocks());
        product.setPrice(productDto.getPrice());
        product.setMrp(productDto.getMrp());
        product.setStoreName(productDto.getStoreName());
        return product;
    }
    public void buyProduct(String id, int quantity) {
        Product product = getProduct(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id " + id);
        }
        if (product.getStocks() < quantity) {
            throw new RuntimeException("Not enough stock available. Current stock: " + product.getStocks());
        }
        product.setStocks(product.getStocks() - quantity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = authentication.getName();
        Customerdetails customerdetails = customerService.getCustomerByName(userName);


        customerdetails.setStoreName(product.getStoreName());

        // Create and save billing details
        Billing billing = new Billing();
        billing.setId(UUID.randomUUID().toString());
        billing.setCustomerUserName(customerdetails.getCustomerName());
        billing.setProductId(product.getId());
        billing.setProductName(product.getProductName());
        billing.setStoreName(product.getStoreName());
        billing.setPrice(product.getPrice());
        billing.setMrp(product.getMrp());
        billing.setPurchaseDate(new Date());
        billing.setQuantity(quantity);

        // Save billing record
        billingService.saveBilling(billing);

        // Update customer billing list
        List<Billing> billingList = customerdetails.getBilling();
        billingList.add(billing);
        customerdetails.setBilling(billingList);

        // Save updated customer details
        customerService.updateCustomer(customerdetails.getCustomerName(), customerdetails);
        String emailBody = String.format(
                "Dear %s,%n%nThank you for your purchase!%n%nProduct: %s%nStore: %s%nQuantity: %d%nPrice: %.2f%nMRP: %.2f%nTotal Cost: %.2f%nDate: %s%n%nBest regards,%nYour Store",
                customerdetails.getCustomerName(),
                product.getProductName(),
                product.getStoreName(),
                quantity,
                product.getPrice(),
                product.getMrp(),
                quantity * product.getPrice(),  // Total cost
                billing.getPurchaseDate()
        );

        // Send email
        emailService.sendEmail(customerdetails.getEmailId(), "Your Purchase Receipt", emailBody);
        // Save updated product
        departmentTemplate.save(product);


    }
    public void cancelProduct(String id, int quantity) {
        Product product = getProduct(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id " + id);
        }
        product.setStocks(product.getStocks() + quantity);
        departmentTemplate.save(product);
    }

}