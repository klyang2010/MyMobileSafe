package com.mymobilesafe.myutils;

/**
 * 加密工具
 */

public class EncryptTools {
    public static String encrypt(String string){
        byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++){
            bytes[i] += 1;
        }
        return  new String(bytes);
    }

    public static String decrypt(String string){
        byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++){
            bytes[i] -= 1;
        }
        return  new String(bytes);
    }

}
