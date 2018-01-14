package com.grooble.model;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class EncryptionProtocol {
    
    private byte[] salt;
    
    public String encryptWithKey(String str, SecretKey secret) {
        try {            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] encryptedText = cipher.doFinal(str.getBytes("UTF-8")); // encrypt the message str here
            
            // concatenate salt + iv + ciphertext
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            outputStream.write(salt);
            outputStream.write(iv);
            outputStream.write(encryptedText);
            
            // properly encode the complete ciphertext
            String encrypted = DatatypeConverter.printBase64Binary(outputStream.toByteArray());
            return encrypted;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String decryptWithKey(String str, SecretKey secret) {
        try {
            byte[] ciphertext = DatatypeConverter.parseBase64Binary(str);
            if (ciphertext.length < 48) {
                return null;
            }
            salt = Arrays.copyOfRange(ciphertext, 0, 16);
            byte[] iv = Arrays.copyOfRange(ciphertext, 16, 32);
            byte[] ct = Arrays.copyOfRange(ciphertext, 32, ciphertext.length);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] plaintext = cipher.doFinal(ct);
            
            return new String(plaintext, "UTF-8");
      
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    // return encryption key
    public SecretKey getKey(String password){
        try {
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // obtain secret key
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); 
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            return secret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // key String (from database) to SecretKey
    public SecretKey getKeyFromString(String keyString){
        byte[] decodedKey = DatatypeConverter.parseBase64Binary(keyString);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return key;
    }
    
    
    public byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            out[i] = (byte) (key[i] ^ a[i%a.length]);
        }
        return out;
    }
}
