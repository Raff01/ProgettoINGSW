package application.model;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class EmailSender {
	private String us;

	public String psw;

	public EmailSender(String us) {
		super();
		this.us = us;
		psw = generatedPassword();
	}

	public void sendEmail(String username, String password, String recipientEmail) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("animalshopigpe@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject("Animal shop: recupero password");

			MimeBodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setContent("Gentile " + us + ", " + '\n' + "il tuo codice è: " + psw, "text/plain");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendEmail(String username, String password, String recipientEmail, String filename) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("animalshopigpe@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject("Animal shop: riepilogo ordine");

			MimeBodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setContent("Gentile " + us + ", " + '\n' + "in allegato troverai il riepilogo dell'ordine","text/plain");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachment = new MimeBodyPart();
			try {
				attachment.attachFile(new File(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			multipart.addBodyPart(attachment);
			message.setContent(multipart);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public String generatedPassword() {
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String upper = lower.toUpperCase();
		String numeri = "0123456789";
		String rnd = upper + lower + numeri;
		int lunghezzaRandom = 9;
		StringBuilder sb = new StringBuilder(lunghezzaRandom);
		Random random = new Random();
		for (int i = 0; i < lunghezzaRandom; i++) {
			int randomInt = random.nextInt(rnd.length());
			char randomChar = rnd.charAt(randomInt);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	public void changePassword() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		Profile p = dbManager.getProfile(us);
		dbManager.closedConnection();
	}

	public String getPass() {
		return psw;
	}
}