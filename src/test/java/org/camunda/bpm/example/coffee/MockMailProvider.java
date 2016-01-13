package org.camunda.bpm.example.coffee;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.mail.Email;
import org.camunda.bpm.example.coffee.mail.MailProvider;

public class MockMailProvider implements MailProvider {

	private final List<Email> sentMails = new LinkedList<Email>();

	public List<Email> getSentMails() {
		return sentMails;
	}

	public void send(Email email) {
		sentMails.add(email);
	}

}
