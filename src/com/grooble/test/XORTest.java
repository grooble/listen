package com.grooble.test;

import javax.xml.bind.DatatypeConverter;

public class XORTest {
    
    public static void main(String[] args){
        String surrogate = args[0];
        String locking = args[1];
        System.out.println("surrogate: " + surrogate + "; locking: " + locking);
        
        byte[] xord = xorWithKey(DatatypeConverter.parseBase64Binary(surrogate), DatatypeConverter.parseBase64Binary(locking));
        System.out.println("xord: " + DatatypeConverter.printBase64Binary(xord));
        
        byte[] recoveredBytes = xorWithKey(xord, DatatypeConverter.parseBase64Binary(locking));
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
}