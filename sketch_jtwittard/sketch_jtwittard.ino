
const char HT1 = 'A'; //Trigger character for hashag 1
const char HT2 = 'B'; //Trigger character for hashag 2
const char HT3 = 'C'; //Trigger character for hashag 3

String HANDSAKING="jTwittard 1.0";

void setup() {
	Serial.begin(9600); //Set comunication to 9600 bauds

	//Set pin 13 to be output pin (Onboard LED)
	pinMode(13, OUTPUT);

	//Set pin 13 LED normally ON
        blink_led();
}

//Blink LED Function
void blink_led() {

	for (int i = 0; i < 7; i++) {
		digitalWrite(13, HIGH);
		delay(100);
		digitalWrite(13, LOW);
		delay(100);
	}

	//Reset the LED to ON
	digitalWrite(13, LOW);
}

void establish_connection(){
    Serial.println(HANDSAKING);
    while (Serial.available() < HANDSAKING.length()+1 ){
      delay(300);
    }  
    for(int i=0; i<HANDSAKING.length()+1; i++){
      Serial.read();
    }
}

//Only called when we get a character via USB/Serial
void got_char(char x) {

	if (x == HT1) {

		blink_led(); //Makes blink the LED
	}

	if (x == HT2) {

		blink_led(); //Makes blink the LED
	}

	if (x == HT3) {

		blink_led(); //Makes blink the LED
	}
}

void loop() { //check if there's any data available on serial
        establish_connection();
        while (Serial.available() == 0){
          delay(300);          
        }	
        byte byte_read = Serial.read();
	got_char((char)byte_read);	
}

