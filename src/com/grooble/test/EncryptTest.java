package com.grooble.test;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.grooble.model.EncryptionProtocol;

public class EncryptTest {
    
    private static final String SALT = "XwM1/8gFIX4OlHYJi7dknQ==";
    // parse salt
    private static byte[] salt = DatatypeConverter.parseBase64Binary(SALT);
    private static String password = "grooble";
    private static String encrypted;
    public static void main(String[] args){
        final String email = "grooble@gmail.com";
        final String recovery = "jessie";
        int h = email.hashCode();
        
        SecretKey recoveryKey1 = getKey(recovery);
        System.out.println("recovery1: " + DatatypeConverter.printBase64Binary(recoveryKey1.getEncoded()));
        
        SecretKey recoveryKey2 = getKey(recovery);
        System.out.println("recovery2: " + DatatypeConverter.printBase64Binary(recoveryKey2.getEncoded()));
        System.out.println("recovery1 length: " + recoveryKey1.getEncoded().length);
        
        byte[] hash1 = null;
        byte[] hash2 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash1 = digest.digest(DatatypeConverter.parseBase64Binary(recovery));
            hash2 = digest.digest(DatatypeConverter.parseBase64Binary(recovery));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("hash1: " + DatatypeConverter.printBase64Binary(hash1));
        System.out.println("hash2: " + DatatypeConverter.printBase64Binary(hash2));
        System.out.println("hash1 length: " + hash1.length);
        

        /*   
        System.out.println("hashcode: " + h);
        
        String toEncrypt = args[0];
        System.out.println("toEncrypt: " + toEncrypt + "\n");
        
        System.out.println("encrypted text: ");
        System.out.println(encrypt(toEncrypt) + "\n");
        
        System.out.println("decrypted text: ");
        System.out.println(decrypt(encrypted) + "\n");
        */ 
    }
    public static String encrypt(String str) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // use the password here to get the secret key
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); 
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            
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
            encrypted = DatatypeConverter.printBase64Binary(outputStream.toByteArray());
            return encrypted;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String decrypt(String str) {
        try {
            byte[] ciphertext = DatatypeConverter.parseBase64Binary(str);
            if (ciphertext.length < 48) {
                return null;
            }
            byte[] salt = Arrays.copyOfRange(ciphertext, 0, 16);
            byte[] iv = Arrays.copyOfRange(ciphertext, 16, 32);
            byte[] ct = Arrays.copyOfRange(ciphertext, 32, ciphertext.length);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
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
    private static SecretKey getKey(String password){
        try {            
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

}