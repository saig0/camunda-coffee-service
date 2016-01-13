package org.camunda.bpm.example.coffee.raspberry;

public interface RaspberryPiController {

	interface RaspberryPiListener {
		void onButtonClicked();
	}

	void turnOnLed();

	void turnOffLed();

	void init(RaspberryPiListener listener);

	void tearDown();

}
