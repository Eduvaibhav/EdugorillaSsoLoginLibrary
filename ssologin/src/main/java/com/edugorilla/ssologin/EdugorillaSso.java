package com.edugorilla.ssologin;



import android.content.Context;
import android.content.Intent;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EdugorillaSso {

    static private String url = null;
    static private int secret_key_location = 0;

    public static void setBaseUrlAndFileLocation(String base_url, int aes_secret_key_location){
        url = base_url;
        secret_key_location = aes_secret_key_location;
    }


    public static void generateLink(String user_info, Context context) throws Exception {
        byte[] private_key = getPrivateKeyData(context);
        byte[][] cipher_and_iv_text = encrypt(stringToByte(user_info), private_key);
        String base64_cipher_text = base64Encode(cipher_and_iv_text[0]);
        String base64_iv = base64Encode(cipher_and_iv_text[1]);
        String full_url = url+"ct="+base64_cipher_text+"&iv="+base64_iv;
        Intent intent = new Intent(context, WebView.class);
        intent.putExtra("url", full_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static byte[] getPrivateKeyData(Context context) throws IOException {
        InputStream ins = context.getResources().openRawResource(secret_key_location);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int size = 0;
        byte[] buffer = new byte[1024];
        while((size=ins.read(buffer,0,1024))>=0){
            outputStream.write(buffer,0,size);
        }
        ins.close();
        buffer = outputStream.toByteArray();
        return buffer;
    }


    private static String base64Encode(byte[] byte_text) {
        return Base64.encodeToString(byte_text, Base64.NO_WRAP);
    }

    private static byte[] stringToByte(String text) {
        try {
            return (text).getBytes("UTF-8");
        } catch(Exception e) {
            return new byte[0];
        }
    }


    public static byte[][] encrypt(byte[] plaintext, byte[] key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key_spec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key_spec);
        byte[] cipherText = cipher.doFinal(plaintext);
        byte[] iv_text = cipher.getIV();
        byte[] storeAllArray [] = {cipherText, iv_text};
        return storeAllArray;
    }
}


