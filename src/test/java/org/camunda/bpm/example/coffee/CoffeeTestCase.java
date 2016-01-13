package org.camunda.bpm.example.coffee;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

public class CoffeeTestCase extends ProcessEngineTestCase {

  @Deployment(resources= {"coffeeProcess.bpmn"})
  public void testHappyPath() {
    VariableMap variables = Variables.createVariables()
      .putValue("email", "philipp.ossler@camunda.com");

    ProcessInstance pi = runtimeService.startProcessInstanceByKey("coffeeProcess", variables);

    Task task = taskService.createTaskQuery().taskCandidateGroup("coffeeMaker").singleResult();
    assertEquals("make coffee", task.getName());
    taskService.complete(task.getId());

    Job job = managementService.createJobQuery().timers().active().singleResult();
    assertNotNull(job);
    managementService.executeJob(job.getId());

    assertProcessEnded(pi.getId());
  }


}
