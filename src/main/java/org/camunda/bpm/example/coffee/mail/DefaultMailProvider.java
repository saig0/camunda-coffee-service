package org.camunda.bpm.example.coffee.mail;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

public class DefaultMailProvider implements MailProvider {

	public void send(Email email) {
		try {
			email.send();
		} catch (EmailException e) {
			throw new RuntimeException("failed to send mail", e);
		}
	}
}