package com.fredmaina.event_management.AuthService.utils;

import java.security.SecureRandom;

public class GenerateCode {


    public static int generateCode() {
        SecureRandom random = new SecureRandom();
        return random.nextInt(6);
    }

}
