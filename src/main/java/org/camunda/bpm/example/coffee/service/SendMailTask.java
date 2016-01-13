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

import java.util.Properties;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendMailTask implements JavaDelegate {

  private Properties mailProperties = new Properties();

  public void execute(DelegateExecution execution) throws Exception {

    mailProperties.load(getClass().getResourceAsStream("/mail.properties"));

    String to = (String) execution.getVariable("email");

    if (to == null) {
      // do nothing
    } else {
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

      try {
        email.setFrom(from);
        email.setSubject("Your Coffee");
        email.setMsg("...is ready to drink.");
        email.addTo(to);

        email.send();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

}
