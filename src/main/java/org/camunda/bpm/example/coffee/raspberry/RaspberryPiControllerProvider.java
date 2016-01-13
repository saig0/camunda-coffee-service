package org.camunda.bpm.example.coffee.raspberry;

public class RaspberryPiControllerProvider {

	private static RaspberryPiController controller;

	public static void setController(RaspberryPiController controller) {
		RaspberryPiControllerProvider.controller = controller;
	}

	public static RaspberryPiController get() {
		return controller;
	}
}
