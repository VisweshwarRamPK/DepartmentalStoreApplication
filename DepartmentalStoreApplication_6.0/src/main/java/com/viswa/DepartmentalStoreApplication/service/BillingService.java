package com.viswa.DepartmentalStoreApplication.service;

import com.viswa.DepartmentalStoreApplication.model.Billing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    @Qualifier("customerTemplate")
    @Autowired
    private MongoTemplate customerTemplate;

    public void saveBilling(Billing billing) {
        customerTemplate.save(billing);
    }


}
