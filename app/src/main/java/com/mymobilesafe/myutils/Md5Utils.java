package com.mymobilesafe.myutils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mrka on 17-2-3.
 */

public class Md5Utils {
    public static String md5(String str) {
        StringBuffer sBuffer = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();
            byte[] digest = md.digest(bytes);
            for (byte b :
                    digest) {
                int d = b & 0xff;
                String s = Integer.toHexString(d);
                if (s.length() == 1){
                    s = "0" + s;
                }
                sBuffer.append(s);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sBuffer + "";
    }
}
