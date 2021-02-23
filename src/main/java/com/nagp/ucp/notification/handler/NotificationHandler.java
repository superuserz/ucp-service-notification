package com.nagp.ucp.notification.handler;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.nagp.ucp.common.constant.QueueConstants;
import com.nagp.ucp.common.request.NotificationPayload;

@Component
public class NotificationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationHandler.class);

	@Autowired
	private JavaMailSender emailSender;

	@Value("${mail.username}")
	private String username;

	@Value("${mail.to}")
	private String to;

	@RabbitListener(queues = QueueConstants.NOTIFICATION_QUEUE)
	public void processNotification(NotificationPayload payload) {

		try {
			LOGGER.info("Request Received : {}", payload.toString());
			send(username, to, payload.getSubject(), payload.getMessageBody());

		} catch (Exception e) {
			LOGGER.error("Exception while processing request : " + e);
		}
	}

	private void send(String from, String to, String subject, String text) {
		final MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

		try {
			message.setSubject(subject);
			message.setTo(to);
			message.setFrom(username);
			message.setText(text);
			emailSender.send(mimeMessage);
			LOGGER.info("Message Sent");

		} catch (MessagingException e) {
			LOGGER.error("Exception while processing request : " + e);
		}
	}

}
