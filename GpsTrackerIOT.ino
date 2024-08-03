#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>

// Provide the token generation process info.
#include "addons/TokenHelper.h"
// Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"
#include <TinyGPS++.h>
#include <HardwareSerial.h>

HardwareSerial GPSSerial(2);
TinyGPSPlus gps;
// Insert your network credentials
#define WIFI_SSID "MifiRizaldi"
#define WIFI_PASSWORD "itama12345"

// Insert Firebase project API Key
#define API_KEY "AIzaSyDqth0_fcH2LEOlSHGZS67-QNIBiNC3oDQ"

// Insert RTDB URL
#define DATABASE_URL "https://tugasskripsi-554c9-default-rtdb.firebaseio.com/"

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;

void setup() {
  Serial.begin(115200);
  GPSSerial.begin(9600, SERIAL_8N1, 16,17);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  auth.user.email = "rizaldipardede1@gmail.com";
  auth.user.password = "itama12345";

  // /* Sign up */
  // if (Firebase.signUp(&config, &auth, "", "")) {
  //   Serial.println("ok");
  //   signupOK = true;
  // } else {
  //   Serial.printf("%s\n", config.signer.signupError.message.c_str());
  // }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  
  if (Firebase.ready()  && (millis() - sendDataPrevMillis > 3000  || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    // Simulated motorbike data
    String phoneNumber = "082274980118";
    String vehicleNumber = "BB 2557 CA";
    gpsEncode();

    double latitude = gps.location.lat(); // Example latitude value
    double longitude = gps.location.lng(); // Example longitude value

    // Write motorbike data to the database
    if (Firebase.RTDB.setString(&fbdo, "motorbikes/" + vehicleNumber + "/phoneNumber", phoneNumber) &&
        Firebase.RTDB.setString(&fbdo, "motorbikes/" + vehicleNumber + "/vehicleNumber", vehicleNumber) &&
        Firebase.RTDB.setDouble(&fbdo, "motorbikes/" + vehicleNumber + "/latitude", latitude) &&
        Firebase.RTDB.setDouble(&fbdo, "motorbikes/" + vehicleNumber + "/longitude", longitude)) {
      Serial.println("Data Sent Successfully");
    } else {
      Serial.println("Failed to Send Data");
      Serial.println("Reason: " + fbdo.errorReason());
    }
  }
  
}
void gpsEncode() {
  while (GPSSerial.available() > 0) {
    if (gps.encode(GPSSerial.read())) {
      if (gps.location.isValid()) {
        Serial.print("Latitude= ");
        Serial.print(gps.location.lat(), 6);
        Serial.print(" Longitude= ");
        Serial.println(gps.location.lng(), 6);
      } else {
        Serial.println("Invalid GPS data");
      }
    }
  }
}
