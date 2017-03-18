package com.mymobilesafe.utils;

/**
 * Created by mrka on 17-2-3.
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
