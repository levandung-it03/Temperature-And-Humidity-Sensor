#include <EspMQTTClient.h>
#include "mqtt_secrets.h"
#include <ESP8266WiFi.h>
#include <DHT.h>

String apiWriteKey = "ZCBR72DSCAC9S2VM";
String apiReadKey = "TC6R21JT5ZSM6S5J";
const char *server = "api.thingspeak.com";

#define DHTPIN D2
#define DHTTYPE DHT11
#define RELAY D0

DHT dht(DHTPIN, DHTTYPE);

WiFiClient client;

void setup() {
  Serial.begin(9600);

  WiFi.begin(SECRET_WIFI_NAME, SECRET_WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  // Initialize Relay Pin and DHT sensor
  pinMode(RELAY, OUTPUT);
  Serial.println("Connected to WiFi");

  dht.begin();
}

void fetchThingSpeakThresholds(
  WiFiClient client,
  float &temperatureThreshold,
  float &humidityThreshold,
  int &speakerStatus) {
  // Create the GET request to read the last field values from the channel
  String getStr = "/channels/YOUR_CHANNEL_ID/fields/1.json?api_key=" + apiReadKey + "&results=1";

  client.println("GET " + getStr + " HTTP/1.1");
  client.println("Host: api.thingspeak.com");
  client.println("Connection: close");
  client.println();

  // Wait for the server response
  while (client.connected() || client.available()) {
    if (client.available()) {
      String line = client.readStringUntil('\n');

      // Parse temperature threshold từ "field3"
      if (line.indexOf("\"field3\":\"") >= 0) {
        int start = line.indexOf("\"field3\":\"") + 9;
        int end = line.indexOf("\"", start);
        temperatureThreshold = line.substring(start, end).toFloat();
      }

      // Parse humidity threshold từ "field4"
      if (line.indexOf("\"field4\":\"") >= 0) {
        int start = line.indexOf("\"field4\":\"") + 9;
        int end = line.indexOf("\"", start);
        humidityThreshold = line.substring(start, end).toFloat();
      }

      // Parse speaker status từ "field5"
      if (line.indexOf("\"field5\":\"") >= 0) {
        int start = line.indexOf("\"field5\":\"") + 9;
        int end = line.indexOf("\"", start);
        speakerStatus = line.substring(start, end).toInt();
      }
    }
  }
}

void loop() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();

  // Check for failed DHT sensor readings
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Failed to read from DHT sensor");
    return;
  }

  // Connect to the ThingSpeak server
  if (client.connect(server, 80)) {
    String postStr = "field1=" + String(temperature) + "&field2=" + String(humidity);

    // Send HTTP POST request
    client.println("POST /update HTTP/1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.println("X-THINGSPEAKAPIKEY: " + apiWriteKey);
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println("Content-Length: " + String(postStr.length()));
    client.println();
    client.println(postStr);

    Serial.print("Temperature: ");
    Serial.print(temperature);
    Serial.print(" °C, Humidity: ");
    Serial.print(humidity);
    Serial.println(" %. Sent data to ThingSpeak.");

    float temperatureThreshold = 0;
    float humidityThreshold = 0;
    int speakerStatus = 1;
    fetchThingSpeakThresholds(client, temperatureThreshold, humidityThreshold, speakerStatus);

    // Relay logic based on temperature and humidity
    if (speakerStatus == 1 && (temperature >= temperatureThreshold || humidity > humidityThreshold)) {
      digitalWrite(RELAY, LOW);  // Deactivate relay
      delay(1000);               // Turning on RELAY
      Serial.println("Relay activated");
    }
  }

  digitalWrite(RELAY, HIGH);  // Deactivate relay
  client.stop();              // Close the connection
  delay(3000);                // Wait 3 seconds before sending the next update
}
