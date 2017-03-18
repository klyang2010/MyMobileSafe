package com.example;

public class MyClass {
    public static void main(String[] args){
        System.out.println("ddd");
    }
    public static void encrypt(String string
    ){
        byte[] bytes = string.getBytes();
        /*for (byte b :
                bytes) {
            b += 1;
        }*/  //此方法只是查询，并不对字节数组做出修改

        for (int i = 0; i < bytes.length; i++){
            bytes[i] += 1;
        }
        String newString = new String(bytes);
        System.out.println(newString);
    }
    
}
