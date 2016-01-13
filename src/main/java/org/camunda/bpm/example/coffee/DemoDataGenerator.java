package org.camunda.bpm.example.coffee;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;
import static org.camunda.bpm.engine.authorization.Permissions.READ;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;
import static org.camunda.bpm.engine.authorization.Resources.FILTER;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.engine.task.TaskQuery;

/**
 * Creates demo credentials to be used in the showcase.
 */
public class DemoDataGenerator {

	private final static Logger LOGGER = Logger.getLogger(DemoDataGenerator.class.getName());

	public void createUsers(ProcessEngine engine) {

		final IdentityService identityService = engine.getIdentityService();

		if (identityService.isReadOnly()) {
			LOGGER.info("Identity service provider is Read Only, not creating any demo users.");
			return;
		}

		User singleResult = identityService.createUserQuery().userId("admin").singleResult();
		if (singleResult != null) {
			return;
		}

		LOGGER.info("Generating demo data for showcase");

		User admin = identityService.newUser("admin");
		admin.setFirstName("Mr.");
		admin.setLastName("Admin");
		admin.setPassword("admin");
		admin.setEmail("admin@camunda.org");
		identityService.saveUser(admin);

		User demoUser = identityService.newUser("demo");
		demoUser.setFirstName("Demo");
		demoUser.setLastName("Demo");
		demoUser.setPassword("demo");
		demoUser.setEmail("demo@camunda.org");

		identityService.saveUser(demoUser);

		Group coffeeMakerGroup = identityService.newGroup("coffeeMaker");
		coffeeMakerGroup.setName("Coffee Maker");
		coffeeMakerGroup.setType("WORKFLOW");
		identityService.saveGroup(coffeeMakerGroup);

		final AuthorizationService authorizationService = engine.getAuthorizationService();

		// create group
		if (identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
			Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
			camundaAdminGroup.setName("camunda BPM Administrators");
			camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
			identityService.saveGroup(camundaAdminGroup);
		}

		// create ADMIN authorizations on all built-in resources
		for (Resource resource : Resources.values()) {
			if (authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource)
					.resourceId(ANY).count() == 0) {
				AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
				userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
				userAdminAuth.setResource(resource);
				userAdminAuth.setResourceId(ANY);
				userAdminAuth.addPermission(ALL);
				authorizationService.saveAuthorization(userAdminAuth);
			}
		}

		identityService.createMembership("admin", "camunda-admin");

		identityService.createMembership("demo", "coffeeMaker");

		// authorize groups for tasklist only:

		Authorization coffeeMakerTasklistAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
		coffeeMakerTasklistAuth.setGroupId("coffeeMaker");
		coffeeMakerTasklistAuth.addPermission(ACCESS);
		coffeeMakerTasklistAuth.setResourceId("tasklist");
		coffeeMakerTasklistAuth.setResource(APPLICATION);
		authorizationService.saveAuthorization(coffeeMakerTasklistAuth);

		Authorization coffeeMakerCockpitAuth = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
		coffeeMakerCockpitAuth.setGroupId("coffeeMaker");
		coffeeMakerCockpitAuth.addPermission(ACCESS);
		coffeeMakerCockpitAuth.setResourceId("cockpit");
		coffeeMakerCockpitAuth.setResource(APPLICATION);
		authorizationService.saveAuthorization(coffeeMakerCockpitAuth);

		Authorization coffeeMakerReadProcessDefinition = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
		coffeeMakerReadProcessDefinition.setGroupId("coffeeMaker");
		coffeeMakerReadProcessDefinition.addPermission(Permissions.ALL);
		coffeeMakerReadProcessDefinition.setResourceId(ANY);
		coffeeMakerReadProcessDefinition.setResource(Resources.PROCESS_DEFINITION);
		authorizationService.saveAuthorization(coffeeMakerReadProcessDefinition);

		Authorization coffeeMakerReadProcessInstance = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
		coffeeMakerReadProcessInstance.setGroupId("coffeeMaker");
		coffeeMakerReadProcessInstance.addPermission(Permissions.ALL);
		coffeeMakerReadProcessInstance.setResourceId(ANY);
		coffeeMakerReadProcessInstance.setResource(Resources.PROCESS_INSTANCE);
		authorizationService.saveAuthorization(coffeeMakerReadProcessInstance);

		// create default filters

		FilterService filterService = engine.getFilterService();

		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put("description", "Tasks assigned to me");
		filterProperties.put("priority", -10);
		TaskService taskService = engine.getTaskService();
		TaskQuery query = taskService.createTaskQuery().taskAssigneeExpression("${currentUser()}");
		Filter myTasksFilter = filterService.newTaskFilter().setName("My Tasks").setProperties(filterProperties)
				.setOwner("demo").setQuery(query);
		filterService.saveFilter(myTasksFilter);

		filterProperties.clear();
		filterProperties.put("description", "Tasks assigned to my Groups");
		filterProperties.put("priority", -5);
		query = taskService.createTaskQuery().taskCandidateGroupInExpression("${currentUserGroups()}").taskUnassigned();
		Filter groupTasksFilter = filterService.newTaskFilter().setName("My Group Tasks")
				.setProperties(filterProperties).setOwner("demo").setQuery(query);
		filterService.saveFilter(groupTasksFilter);

		// global read authorizations for these filters

		Authorization globalMyTaskFilterRead = authorizationService
				.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
		globalMyTaskFilterRead.setResource(FILTER);
		globalMyTaskFilterRead.setResourceId(myTasksFilter.getId());
		globalMyTaskFilterRead.addPermission(READ);
		authorizationService.saveAuthorization(globalMyTaskFilterRead);

		Authorization globalGroupFilterRead = authorizationService
				.createNewAuthorization(Authorization.AUTH_TYPE_GLOBAL);
		globalGroupFilterRead.setResource(FILTER);
		globalGroupFilterRead.setResourceId(groupTasksFilter.getId());
		globalGroupFilterRead.addPermission(READ);
		authorizationService.saveAuthorization(globalGroupFilterRead);

		// management filter

		filterProperties.clear();
		filterProperties.put("description", "Coffee Maker Tasks");
		filterProperties.put("priority", -3);
		query = taskService.createTaskQuery().taskCandidateGroupIn(Arrays.asList("coffeeMaker")).taskUnassigned();
		Filter candidateGroupTasksFilter = filterService.newTaskFilter().setName("CoffeeMaker")
				.setProperties(filterProperties).setOwner("admin").setQuery(query);
		filterService.saveFilter(candidateGroupTasksFilter);

		Authorization managementGroupFilterRead = authorizationService
				.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		managementGroupFilterRead.setResource(FILTER);
		managementGroupFilterRead.setResourceId(candidateGroupTasksFilter.getId());
		managementGroupFilterRead.addPermission(READ);
		managementGroupFilterRead.setGroupId("coffeeMaker");
		authorizationService.saveAuthorization(managementGroupFilterRead);
	}

}
