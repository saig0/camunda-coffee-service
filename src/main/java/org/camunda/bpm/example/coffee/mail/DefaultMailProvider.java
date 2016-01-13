package org.camunda.bpm.example.coffee.mail;

import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

public class DefaultMailProvider implements MailProvider {

	private final static Logger LOGGER = Logger.getLogger(DefaultMailProvider.class.getName());

	public void send(Email email) {
		LOGGER.info("send mail to '" + email.getToAddresses() + "'");

		try {
			email.send();
		} catch (EmailException e) {
			LOGGER.info("failed to send mail");

			throw new RuntimeException("failed to send mail", e);
		}
	}
}