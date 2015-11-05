package com.qlik.automation;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class MailUtil {
	final static Logger logger = Logger.getLogger(CronTrigger.class);

	public static void sendReports(String fileLocation, String fileName)
			throws Exception {
		try {
			logger.debug("Sending email started");
			Properties properties = PropertiesUtil.getApplicationProperties();
			String username = properties.getProperty("smtp.userName");
			String password = properties.getProperty("smtp.password");

			final String userNameFinal = username;
			final String passwordFinal = password;

			Properties props = new Properties();
			props.put("mail.smtp.auth", properties.getProperty("smtp.auth"));
			props.put("mail.smtp.starttls.enable", properties.getProperty("smtp.starttls.enable"));
			props.put("mail.smtp.host", properties.getProperty("stmp.host"));
			props.put("mail.smtp.port", properties.getProperty("stmp.port"));

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(userNameFinal,
									passwordFinal);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(properties.getProperty("dailyReport.fromAddress")));

			String toEmails[] = properties.getProperty("dailyReport.recipients").split(",");
			InternetAddress[] addresses = new InternetAddress[toEmails.length];
			for (int i = 0; i < toEmails.length; i++) {
				addresses[i] = new InternetAddress(toEmails[i]);
			}
			message.setRecipients(Message.RecipientType.TO, addresses);
			message.setSubject("Daily Reports");
			message.setText("Dear Mail Crawler, No spam to my email, please!");

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Daily report.");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileLocation);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			logger.debug("Email send succesfull.");
		} catch (Exception ex) {
			logger.error(ex, ex);
			throw ex;
		}
	}

}