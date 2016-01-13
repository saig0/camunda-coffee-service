package org.camunda.bpm.example.coffee.mail;

import org.apache.commons.mail.Email;

public interface MailProvider {

	void send(Email email);
}