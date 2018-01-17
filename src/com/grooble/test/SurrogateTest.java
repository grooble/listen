package com.grooble.test;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class SurrogateTest {
    private static final String alphanumeric =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "1234567890"
            + "!#$%&()+*<>?_-=^~|";

    private static SecureRandom rnd = new SecureRandom();

    private static byte[] salt;

    public static void main(String[] args) {
        // arguments are password and recovery string
        if(args.length>1){            
            System.out.println("password: " + args[0] + "; recovery: " + args[1]);
        }
        String password = args[0];
        String recovery = args[1];
        
        // passwordKey
        SecretKey passwordKey = getKey(password);
        System.out.println("passwordKey: " + DatatypeConverter.printBase64Binary(passwordKey.getEncoded()));
        
        SecretKey recoveryKey = getKey(recovery);
        System.out.println("recoveryKey: " + DatatypeConverter.printBase64Binary(recoveryKey.getEncoded()));

        // Generate surrogate encryption key from random string
        String rand = randomString(24);
        SecretKey surrogateKey = getKey(rand);
        byte[] surrogateByteArray = surrogateKey.getEncoded();
        System.out.println("surrogate: " + DatatypeConverter.printBase64Binary(surrogateByteArray));

        // XOR surrogateKey with passwordKey to get storedKey
        SecretKey storedKey = xorWithKey(surrogateKey, passwordKey);
        String storedKeyString = DatatypeConverter.printBase64Binary(storedKey.getEncoded());
        System.out.println("storedKey: "+ storedKeyString);
        
        byte[] storedKey2Array = DatatypeConverter.parseBase64Binary(storedKeyString);
        SecretKey storedKey2 = new SecretKeySpec(storedKey2Array, 0, storedKey2Array.length, "AES");
        String storedKey2String = DatatypeConverter.printBase64Binary(storedKey2.getEncoded());
        System.out.println("storedKey->String->key->string: " + storedKey2String);
        
        // XOR surrogateKey with recoveryKey to get backupKey
        SecretKey backupKey = xorWithKey(surrogateKey, recoveryKey);
        System.out.println("backupKey: "+ DatatypeConverter.printBase64Binary(backupKey.getEncoded()));
    }
    
    private static SecretKey xorWithKey(SecretKey a, SecretKey b) {
        byte[] out = new byte[b.getEncoded().length];
        for (int i = 0; i < b.getEncoded().length; i++) {
            out[i] = (byte) (b.getEncoded()[i] ^ a.getEncoded()[i%a.getEncoded().length]);
        }
        SecretKey outKey = new SecretKeySpec(out, 0, out.length, "AES");

        return outKey;
    }
    
    private static String randomString(int length){
        StringBuilder sb = new StringBuilder(length);
        for( int i = 0; i < length; i++ ) 
           sb.append( alphanumeric.charAt( rnd.nextInt(alphanumeric.length()) ) );
        return sb.toString();
    }

    // return encryption key
    public static SecretKey getKey(String password){
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


}
