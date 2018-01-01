package com.grooble.model;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class JMailer {
	public void sendMail(String toUser, String fromUser, String subject, String body) {
		//fromUser = "admin@moeigo.com";
		String jSubject = subject;
		String jBody = body;
		
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "mail.moeigo.com");
		properties.put("mail.smtp.port", "25");
		Session session = Session.getDefaultInstance(properties, null);
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(fromUser));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toUser));
			message.setSubject(jSubject);
			message.setText(jBody);
			Transport.send(message);
		} 
		catch (AddressException e) {
			e.printStackTrace();
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}