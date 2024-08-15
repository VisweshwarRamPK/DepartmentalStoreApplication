package com.viswa.DepartmentalStoreApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("Billing")
public class Billing {

    @Id
    private String id;
    private String customerUserName;
    private String productId;
    private String productName;
    private String storeName;
    private double price;
    private double mrp;
    private Date purchaseDate;
    private int quantity;
}
