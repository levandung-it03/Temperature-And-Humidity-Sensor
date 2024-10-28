#include <EspMQTTClient.h>
#include "mqtt_secrets.h"
#include <ESP8266WiFi.h>
#include <DHT.h>

const String apiWriteKey = "ZCBR72DSCAC9S2VM";
const String apiReadKey = "TC6R21JT5ZSM6S5J";
const String apiThresholdWriteKey = "X4IQSOK40JDTXKCY";
const String apiThresholdReadKey = "RSJ6MDJBWBVG6O0D";
const String server = "api.thingspeak.com";

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

void fetchThingSpeakThresholds(WiFiClient client, float &temperatureThreshold, float &humidityThreshold, int &speakerStatus) {
  if (client.connect(server, 80)) {
    String getStr = "/channels/" + String(THRESHOLD_CHANNEL_ID) + "/feeds.json?api_key=" + apiThresholdReadKey + "&results=1";

    client.println("GET " + getStr + " HTTP/1.1");
    client.println("Host: " + server);
    client.println("Connection: close");
    client.println();

    // Wait for the server response
    while (client.connected() || client.available()) {
      if (client.available()) {
        String line = client.readStringUntil('\n');
        int feedsDataIndexStart = line.indexOf("feeds");
        if (feedsDataIndexStart == -1)
          continue;

        String data = line.substring(feedsDataIndexStart, line.length());

        // Parse temperature threshold từ "field1"
        if (data.indexOf("field1") >= 0) {
          int start = data.indexOf("field1") + 9;
          int end = data.indexOf("\"", start);
          temperatureThreshold = data.substring(start, end).toFloat();
        }

        // Parse humidity threshold từ "field2"
        if (data.indexOf("field2") >= 0) {
          int start = data.indexOf("field2") + 9;
          int end = data.indexOf("\"", start);
          humidityThreshold = data.substring(start, end).toFloat();
        }

        // Parse speaker status từ "field3"
        if (data.indexOf("field3") >= 0) {
          int start = data.indexOf("field3") + 9;
          int end = data.indexOf("\"", start);
          speakerStatus = data.substring(start, end).toInt();
        }
      }
    }
    Serial.print("Temperature Threshold: ");
    Serial.println(temperatureThreshold);
    Serial.print("Humidity Threshold: ");
    Serial.println(humidityThreshold);
    Serial.print("Speaker Status: ");
    Serial.println(speakerStatus);
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

  float temperatureThreshold = 0;
  float humidityThreshold = 0;
  int speakerStatus = 1;
  fetchThingSpeakThresholds(client, temperatureThreshold, humidityThreshold, speakerStatus);

  // Connect to the ThingSpeak server
  if (client.connect(server, 80)) {
    String postStr = "field1=" + String(temperature) + "&field2=" + String(humidity);

    // Send HTTP POST request
    client.println("POST /update HTTP/1.1");
    client.println("Host: " + server);
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

    // Relay logic based on temperature and humidity
    if (speakerStatus == 1 && (temperature >= temperatureThreshold || humidity > humidityThreshold)) {
      digitalWrite(RELAY, LOW);  // Deactivate relay
      delay(200);  // Turning on RELAY
      Serial.println("Relay activated");
    }
  }

  digitalWrite(RELAY, HIGH);  // Deactivate relay
  client.stop();              // Close the connection
  delay(3000);                // Wait 3 seconds before sending the next update
}
