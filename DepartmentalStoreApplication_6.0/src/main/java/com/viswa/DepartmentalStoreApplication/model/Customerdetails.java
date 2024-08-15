package com.viswa.DepartmentalStoreApplication.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Customerdetails")

public class Customerdetails {
    private String customerName;

    private String storeName;
    private String phoneNumber;
    private String emailId;
    private String address;
    List<Billing> billing;
}
