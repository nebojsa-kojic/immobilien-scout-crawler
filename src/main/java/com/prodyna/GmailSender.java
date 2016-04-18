package com.prodyna;

import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends E-Mail.
 *
 * @author Boris Gligorijevic, PRODYNA AG
 */
public class GmailSender {

	public static void send(Set<Apartment> newApartments) {

		String from = PropertiesUtil.getPropFromFile(1);
		String pass = PropertiesUtil.getPropFromFile(2);
		String[] to = PropertiesUtil.getPropFromFile(3).split(",");

		String subject = "New apartments in Munich";

		StringBuilder links = new StringBuilder();
		for (Apartment apartment : newApartments) {
			links.append("<a href=\"" + getApartmentUrl(apartment.getId()) + "\">" + apartment.getId() + "</a> travel duration: " + apartment.getTravelTime() + " <br>");
		}

		sendFromGMail(from, pass, to, subject, links.toString());
	}

	private static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			message.setText(body, "utf-8", "html");

			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (AddressException ae) {
			ae.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}

	private static String getApartmentUrl(String apartmentId) {
		return App.WEBSITE_ROOT + "/expose/" + apartmentId;
	}
}
