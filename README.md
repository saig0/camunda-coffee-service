# camunda-coffee-service

Request a coffee by starting a new process instance of the coffee-process. This opens a user task and switch on a led on the raspberry pi. When the user starts making the coffee then he push the button on the rapsberry pi which completes the user task. After 5 minutes, the user who requested the coffee will receive an email.

## Requirements

* raspberry pi
* breadboard + jumper wires
* led + resistor (on pin 17)
* tactile buttons (on pin 27)

## How to run

* build the WAR archive
* [download](https://camunda.org/download/) the camunda distro for tomcat
* replace the invoice example with the WAR
* start the tomcat server
 
