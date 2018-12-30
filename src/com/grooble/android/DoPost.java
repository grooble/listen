package com.grooble.android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.grooble.model.JSONMaker;
import com.grooble.model.MyDebug;


public class DoPost {
	
	public static final String TAG = "DoPost: ";

	String apikey = "50dc8f9cd08d73e1d31cb1019eaaead0-us19";
	String listId = "d0c934e6e8";
	String email;

	public DoPost(String email){
		this.email = email;
	}

	public void doPostAction() {
		
		// Populate client with parameters
		String url = "https://us19.api.mailchimp.com/3.0/lists/"
				+ listId
				+ "/members";

		JSONObject job = new JSONMaker().toJSONParams(email);
		String data = "";
		if(MyDebug.LOGINLOG){
			System.out.println("DoPost JSON: " + job.toString()); //JSON of login params
		}
		data = job.toString();

		// BASIC Authentication
		String name = "user";
		String password = apikey;     //MailChimp API key
		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		URL urlConnector;
		DataOutputStream dos   = null;
		InputStream is    = null;
		BufferedReader br = null;
				
		try {
			urlConnector = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection) urlConnector.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
						
			dos = new DataOutputStream(httpConnection.getOutputStream());
	
			if(MyDebug.LOGINLOG){
				System.out.println(TAG + "URL: " + httpConnection.getURL().toString());
				System.out.println(TAG + "toWrite: " + data);
			}
			dos.writeBytes(data);
			if(MyDebug.LOGINLOG){
				System.out.println(TAG + "status: " + httpConnection.getResponseCode());
			}
			
			is = httpConnection.getInputStream();
			
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			if(MyDebug.LOGINLOG){
				System.out.println("DoPost response: \n" + sb);
			}
			/*
		 */
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException ue){
			ue.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		finally{
			if(dos != null){
				try {
					dos.flush();
					dos.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
					if(is != null){
				try {
					is.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
		}
		
	}
	
	public void patchName(String fname, String lname){
		
		String url = "https://us19.api.mailchimp.com/3.0/lists/"
				+ listId
				+ "/members/";

		String patch = "{\"merge_fields\":{\"FNAME\": \"" + fname + "\",\"LNAME\": \"" + lname + "\"}}";
		if(MyDebug.LOGINLOG){
			System.out.println(TAG + "patch: " + patch);
		}
		
		// Get MD5 of email
		MessageDigest md = null;
		String emailMD5 = "";
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] emailBytes = md.digest(email.getBytes("UTF-8"));
			emailMD5 = emailBytes.toString();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		url = url + emailMD5;
		if(MyDebug.LOGINLOG){
			System.out.println(TAG + "name URL: " + url);
		}
		
		// BASIC Authentication
		String name = "user";
		String password = apikey;     //MailChimp API key
		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		URL urlConnector;
		DataOutputStream dos   = null;
		InputStream is    = null;
		BufferedReader br = null;
				
		try {
			urlConnector = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection) urlConnector.openConnection();
			httpConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
						
			dos = new DataOutputStream(httpConnection.getOutputStream());
	
			if(MyDebug.LOGINLOG){
				System.out.println(TAG + "URL: " + httpConnection.getURL().toString());
				System.out.println(TAG + "toWrite: " + patch);
			}
			dos.writeBytes(patch);
			if(MyDebug.LOGINLOG){
				System.out.println(TAG + "status: " + httpConnection.getResponseCode());
			}
			
			is = httpConnection.getInputStream();
			
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			if(MyDebug.LOGINLOG){
				System.out.println("DoPost response: \n" + sb);
			}
			/*
		 */
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException ue){
			ue.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		finally{
			if(dos != null){
				try {
					dos.flush();
					dos.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
					if(is != null){
				try {
					is.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException ioe) {ioe.printStackTrace();}
			}
		}

	}

	/*
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        //result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        //result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	*/
}