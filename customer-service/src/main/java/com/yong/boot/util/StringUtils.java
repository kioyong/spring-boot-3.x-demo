package com.yong.boot.util;

import lombok.extern.log4j.Log4j2;

import java.security.SecureRandom;

import static com.yong.boot.util.LogUtils.application;

@Log4j2
public class StringUtils {

    private StringUtils() {

    }

    private static final String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    public static String generateRandomString(int numChar) {
        try {
            StringBuilder shortBuffer = new StringBuilder();
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < numChar; i++) {
                int x = secureRandom.nextInt(chars.length - 1);
                shortBuffer.append(chars[x]);
            }
            return shortBuffer.toString();
        } catch (Exception ex) {
            log.error(application, "generate Random String error: {}", ex.getMessage());
            return "";
        }
    }


}