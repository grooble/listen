package com.grooble.test;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

import com.grooble.model.EncryptionProtocol;

public class XORTest {
    
    public static void main(String[] args){
        String surrogate = args[0];
        String locking = args[1];
        System.out.println("surrogate: " + surrogate + "; locking: " + locking);
        
        String surrogateKey = getKey(surrogate);
        String lockingKey = getKey(locking);
        System.out.println("surrogateKey: " + surrogateKey);
        System.out.println("key lengths: " + surrogateKey.length() + ", " + lockingKey.length());
         
        byte[] xord = xorWithKey(DatatypeConverter.parseBase64Binary(surrogateKey), DatatypeConverter.parseBase64Binary(lockingKey));
        System.out.println("xord: " + DatatypeConverter.printBase64Binary(xord));
        
        byte[] recoveredBytes = xorWithKey(xord, DatatypeConverter.parseBase64Binary(lockingKey));
        System.out.println("recovered surrogate: " + DatatypeConverter.printBase64Binary(recoveredBytes));
    }

    public String encode(String s, String key) {
        return DatatypeConverter.printBase64Binary(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public String decode(String s, String key) {
        return new String(xorWithKey(DatatypeConverter.parseBase64Binary(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            out[i] = (byte) (key[i] ^ a[i%a.length]);
        }
        return out;
    }
    
    // return encryption key
    private static String getKey(String password){
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // obtain secret key
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); 
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            String charKey = Hex.encodeHexString(secret.getEncoded());
            return charKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}