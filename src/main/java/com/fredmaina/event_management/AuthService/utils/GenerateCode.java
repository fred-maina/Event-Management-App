package com.fredmaina.event_management.AuthService.utils;

import java.security.SecureRandom;

public class GenerateCode {


    public static int generateCode() {
        SecureRandom random = new SecureRandom();
        return 100000+random.nextInt(900000);
    }

}
