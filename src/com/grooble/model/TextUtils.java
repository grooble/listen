package com.grooble.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TextUtils {
	/*
	 * Various text formatting utilities
	 */
	
	// replaces the ' character with ''
	// to make SQL injection more difficult
	public String replace(String text){
		String processed = text;
		//System.out.println("TextUtils-->text1: " + processed);
		int index = 0;
		int l = processed.length();
		index = processed.indexOf('\'');
		StringBuffer buff = new StringBuffer(processed);
		while((index != -1)&&(index<l)){
				char ch = '\'';
				if((index+1<l)&&(buff.charAt(index+1) != ('\''))){
					buff.insert(index, ch);
					l++;
				}
				index++;
				index = buff.indexOf("\'", index+1);
				if(index==-1){
					processed = buff.toString();
				}
		}
		//System.out.println("TextUtils-->text2: " + processed);
		return processed;
	}
	
	public String makeCode(String email) throws NoSuchAlgorithmException{
		String plaintext = email;
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(plaintext.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		return hashtext;
	}
}
