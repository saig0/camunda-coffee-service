package org.camunda.bpm.example.coffee.raspberry;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class DefaultRaspberryPiController implements RaspberryPiController {

	private GpioController gpio;

	private GpioPinDigitalOutput led;
	private GpioPinDigitalInput button;

	public void init(final RaspberryPiListener listener) {
		gpio = GpioFactory.getInstance();

		led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "LED", PinState.LOW);
		button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

		button.addListener(new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState().isLow()) {
					listener.onButtonClicked();
				}
			}
		});
	}

	public void tearDown() {
		gpio.shutdown();
	}

	public void turnOnLed() {
		led.high();
	}

	public void turnOffLed() {
		led.low();
	}

}
