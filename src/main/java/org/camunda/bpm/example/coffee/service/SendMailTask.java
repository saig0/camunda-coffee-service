/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.example.coffee.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.example.coffee.mail.DefaultMailProvider;
import org.camunda.bpm.example.coffee.mail.MailProvider;

public class SendMailTask implements JavaDelegate {

	private Properties mailProperties;
	private static MailProvider mailProvider;

	public void execute(DelegateExecution execution) throws Exception {

		loadProperties();

		String to = (String) execution.getVariable("email");

		if (to == null) {
			// do nothing
		} else {
			sendMail(to);
		}

	}

	private void sendMail(String to) throws EmailException {
		String host = mailProperties.getProperty("host");
		Integer port = Integer.valueOf(mailProperties.getProperty("port"));
		String user = mailProperties.getProperty("user");
		String password = mailProperties.getProperty("password");

		String from = mailProperties.getProperty("from");

		Email email = new SimpleEmail();
		email.setHostName(host);
		email.setTLS(true);
		email.setSmtpPort(port);
		email.setAuthentication(user, password);

		email.setFrom(from);
		email.setSubject("Your Coffee");
		email.setMsg("...is ready to drink.");
		email.addTo(to);

		getMailProvider().send(email);
	}

	private MailProvider getMailProvider() {
		if (mailProvider == null) {
			mailProvider = new DefaultMailProvider();
		}
		return mailProvider;
	}

	private void loadProperties() throws IOException {
		if (mailProperties == null) {
			mailProperties = new Properties();
			InputStream inputStream = getClass().getResourceAsStream("/mail.properties");

			if (inputStream == null) {
				throw new RuntimeException("mail properties not found!");
			}

			mailProperties.load(inputStream);
		}
	};

	public static void setMailProvider(MailProvider mailProvider) {
		SendMailTask.mailProvider = mailProvider;
	}
}
