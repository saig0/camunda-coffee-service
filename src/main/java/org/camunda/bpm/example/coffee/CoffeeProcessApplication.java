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
package org.camunda.bpm.example.coffee;

import java.util.List;
import java.util.logging.Logger;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.PreUndeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.example.coffee.raspberry.DefaultRaspberryPiController;
import org.camunda.bpm.example.coffee.raspberry.RaspberryPiController.RaspberryPiListener;
import org.camunda.bpm.example.coffee.raspberry.RaspberryPiControllerProvider;

/**
 * Process Application exposing this application's resources the process engine.
 */
@ProcessApplication
public class CoffeeProcessApplication extends ServletProcessApplication {

	private final static Logger LOGGER = Logger.getLogger(CoffeeProcessApplication.class.getName());

	private DefaultRaspberryPiController raspberryPiController;

	/**
	 * In a @PostDeploy Hook you can interact with the process engine and access
	 * the processes the application has deployed.
	 */
	@PostDeploy
	public void startFirstProcess(ProcessEngine processEngine) {

		createUsers(processEngine);

		initRaspberryPiController(processEngine);
	}

	private void createUsers(ProcessEngine processEngine) {
		new DemoDataGenerator().createUsers(processEngine);
	}

	private void initRaspberryPiController(final ProcessEngine processEngine) {
		LOGGER.info("init raspberry pi controller");

		raspberryPiController = new DefaultRaspberryPiController();

		raspberryPiController.init(new RaspberryPiListener() {

			public void onButtonClicked() {
				LOGGER.info("button clicked on raspberry pi - complete tasks");

				TaskService taskService = processEngine.getTaskService();

				List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("coffeeMaker").list();

				for (Task task : tasks) {
					taskService.complete(task.getId());
				}
			}
		});

		RaspberryPiControllerProvider.setController(raspberryPiController);

		LOGGER.info("raspberry pi controller initialized");
	}

	@PreUndeploy
	public void cleanup(ProcessEngine processEngine) {
		raspberryPiController.tearDown();
	}
}
