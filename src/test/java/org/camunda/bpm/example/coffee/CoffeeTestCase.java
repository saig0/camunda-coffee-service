package org.camunda.bpm.example.coffee;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.example.coffee.raspberry.RaspberryPiControllerProvider;
import org.camunda.bpm.example.coffee.service.SendMailTask;

public class CoffeeTestCase extends ProcessEngineTestCase {

	private MockMailProvider mockMailProvider = new MockMailProvider();
	private MockRaspberryPiController mockRaspberryPiController = new MockRaspberryPiController();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		SendMailTask.setMailProvider(mockMailProvider);
		RaspberryPiControllerProvider.setController(mockRaspberryPiController);
	}

	@Deployment(resources = { "coffeeProcess.bpmn" })
	public void testHappyPath() {
		VariableMap variables = Variables.createVariables().putValue("email", "philipp.ossler@camunda.com");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("coffeeProcess", variables);

		assertTrue(mockRaspberryPiController.isLedTurnedOn());

		Task task = taskService.createTaskQuery().taskCandidateGroup("coffeeMaker").singleResult();

		assertEquals("make coffee", task.getName());
		taskService.complete(task.getId());

		assertFalse(mockRaspberryPiController.isLedTurnedOn());

		Job job = managementService.createJobQuery().timers().active().singleResult();

		assertNotNull(job);
		managementService.executeJob(job.getId());

		assertEquals(1, mockMailProvider.getSentMails().size());

		assertProcessEnded(pi.getId());
	}

}
