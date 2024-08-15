package com.viswa.DepartmentalStoreApplication.config;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
    private String phoneNumber;
    private String emailId;
    private String address;
}

