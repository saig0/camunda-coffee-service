package org.camunda.bpm.example.coffee;

import org.camunda.bpm.example.coffee.raspberry.RaspberryPiController;

public class MockRaspberryPiController implements RaspberryPiController {

	private boolean isLedTurnedOn = false;

	public boolean isLedTurnedOn() {
		return isLedTurnedOn;
	}

	public void turnOnLed() {
		isLedTurnedOn = true;
	}

	public void turnOffLed() {
		isLedTurnedOn = false;
	}

	public void init(RaspberryPiListener listener) {
		isLedTurnedOn = false;
	}

	public void tearDown() {
		isLedTurnedOn = false;
	}

}
