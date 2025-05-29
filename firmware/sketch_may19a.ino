#include <WiFi.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

// WiFi credentials
const char* ssid = "Quỳnh Lông 🤡";
const char* password = "23072004";

// WebSocket
WebSocketsClient webSocket;

// Pins
#define IR_SENSOR_PIN       26
#define IR_SENSOR_OUT_PIN   27
#define SERVO_PIN           25
#define SERVO_OUT_PIN       23
#define IR_A1_PIN           12
#define IR_A2_PIN           13
#define IR_A3_PIN           14
#define IR_A4_PIN           15
#define IR_B1_PIN           16
#define IR_B2_PIN           17

// State
bool detectionSent = false;
bool detectionOutSent = false;
bool servoInRotated = false;
bool servoOutRotated = false;
unsigned long lastObjectSeenTime = 0;
unsigned long lastObjectOutSeenTime = 0;
unsigned long resetDelay = 2000;

bool a1LastState = false, a2LastState = false, a3LastState = false, a4LastState = false;
bool b1LastState = false, b2LastState = false;

Servo servoIn;
Servo servoOut;

void setup() {
  Serial.begin(115200);
  setupPins();
  setupServos();
  connectToWiFi();
  setupWebSocket();
}

void loop() {
  webSocket.loop();
  handleDetection();
  handleDetectionOut();
  handleServoReset();
  monitorSlots();
}

// === Setup Functions ===
void setupPins() {
  pinMode(IR_SENSOR_PIN, INPUT);
  pinMode(IR_SENSOR_OUT_PIN, INPUT);

  // Chỉ cắm sensor vào IR_SENSOR_PIN, các chân slot (A1-A4, B1-B2) chưa dùng => kéo lên HIGH mặc định
  pinMode(IR_A1_PIN, INPUT_PULLUP);
  pinMode(IR_A2_PIN, INPUT_PULLUP);
  pinMode(IR_A3_PIN, INPUT_PULLUP);
  pinMode(IR_A4_PIN, INPUT_PULLUP);
  pinMode(IR_B1_PIN, INPUT_PULLUP);
  pinMode(IR_B2_PIN, INPUT_PULLUP);
}


void setupServos() {
  servoIn.attach(SERVO_PIN, 500, 2400);
  servoOut.attach(SERVO_OUT_PIN, 500, 2400);
  servoIn.write(0);
  servoOut.write(0);
}

void connectToWiFi() {
  Serial.printf("[WiFi] Connecting to %s\n", ssid);
  WiFi.begin(ssid, password);
  unsigned long start = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - start < 15000) {
    delay(500);
    Serial.print(".");
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[WiFi] Connected.");
  } else {
    Serial.println("\n[WiFi] Failed to connect! Halting...");
    while (true) delay(1000);
  }
}

void setupWebSocket() {
  webSocket.begin("172.20.10.13", 3000, "/");
  webSocket.onEvent(webSocketEvent);
  webSocket.setReconnectInterval(3000);
}

// === Detection Handling ===
void handleDetection() {
  bool detectionNow = (digitalRead(IR_SENSOR_PIN) == LOW);
  unsigned long currentMillis = millis();

  if (detectionNow) {
    lastObjectSeenTime = currentMillis;
    if (!detectionSent) {
      sendDetection("detection");
      detectionSent = true;
    }
  } else if (currentMillis - lastObjectSeenTime > resetDelay) {
    detectionSent = false;
  }
}

void handleDetectionOut() {
  bool detectionNow = (digitalRead(IR_SENSOR_OUT_PIN) == LOW);
  unsigned long currentMillis = millis();

  if (detectionNow) {
    lastObjectOutSeenTime = currentMillis;
    if (!detectionOutSent) {
      sendDetection("detection_out");
      detectionOutSent = true;
    }
  } else if (currentMillis - lastObjectOutSeenTime > resetDelay) {
    detectionOutSent = false;
  }
}

void sendDetection(const char* eventType) {
  Serial.printf("[WS] Gửi sự kiện %s\n", eventType);
  if (webSocket.isConnected()) {
    StaticJsonDocument<128> doc;
    doc["event"] = eventType;
    String jsonStr;
    serializeJson(doc, jsonStr);
    webSocket.sendTXT(jsonStr);
  } else {
    Serial.println("[WS] Không gửi được: WebSocket chưa kết nối.");
  }
}

// === Servo Reset ===
void handleServoReset() {
  unsigned long currentMillis = millis();

  if (servoInRotated && digitalRead(IR_SENSOR_PIN) != LOW && currentMillis - lastObjectSeenTime > resetDelay) {
    Serial.println("[Servo In] Reset về 0");
    servoIn.write(0);
    servoInRotated = false;
  }

  if (servoOutRotated && digitalRead(IR_SENSOR_OUT_PIN) != LOW && currentMillis - lastObjectOutSeenTime > resetDelay) {
    Serial.println("[Servo Out] Reset về 0");
    servoOut.write(0);
    servoOutRotated = false;
  }
}

// === Slot Monitoring ===
void monitorSlots() {
  checkAndSendSlot(IR_A1_PIN, "A1", a1LastState);
  checkAndSendSlot(IR_A2_PIN, "A2", a2LastState);
  checkAndSendSlot(IR_A3_PIN, "A3", a3LastState);
  checkAndSendSlot(IR_A4_PIN, "A4", a4LastState);
  checkAndSendSlot(IR_B1_PIN, "B1", b1LastState);
  checkAndSendSlot(IR_B2_PIN, "B2", b2LastState);
}

void checkAndSendSlot(int pin, const char* slotName, bool& lastState) {
  bool currentState = (digitalRead(pin) == LOW);
  if (currentState != lastState) {
    lastState = currentState;
    sendSlotStatus(slotName, currentState);
  }
}

void sendSlotStatus(const char* slot, bool inUse) {
  if (webSocket.isConnected()) {
    StaticJsonDocument<128> doc;
    doc["event"] = "slot_status";
    doc["slot"] = slot;
    doc["in_use"] = inUse;

    String jsonStr;
    serializeJson(doc, jsonStr);
    webSocket.sendTXT(jsonStr);
    Serial.printf("[WS] Gửi trạng thái %s: %s\n", slot, inUse ? "IN_USE" : "AVAILABLE");
  }
}

// === WebSocket Event Handler ===
void webSocketEvent(WStype_t type, uint8_t* payload, size_t length) {
  if (type == WStype_TEXT) {
    Serial.printf("[WS] Nhận lệnh: %s\n", payload);

    StaticJsonDocument<128> doc;
    DeserializationError error = deserializeJson(doc, payload);
    if (error) {
      Serial.print("[JSON] Lỗi parse: ");
      Serial.println(error.c_str());
      return;
    }

    const char* command = doc["command"];
    const char* target = doc["target"];

    if (strcmp(command, "rotate") == 0) {
      if (target) {
        if (strcmp(target, "in") == 0 && !servoInRotated) {
          rotateServo(servoIn, servoInRotated, "[Servo In]");
        } else if (strcmp(target, "out") == 0 && !servoOutRotated) {
          rotateServo(servoOut, servoOutRotated, "[Servo Out]");
        } else {
          Serial.println("[WS] Target không hợp lệ hoặc servo đã quay.");
        }
      } else {
        Serial.println("[WS] Lệnh thiếu target.");
      }
    }
  } else if (type == WStype_DISCONNECTED) {
    Serial.println("[WS] WebSocket ngắt kết nối");
  } else if (type == WStype_CONNECTED) {
    Serial.println("[WS] WebSocket kết nối thành công");
  }
}

// === Servo Rotation ===
void rotateServo(Servo& servo, bool& rotatedFlag, const char* logPrefix) {
  Serial.printf("%s Quay 90 độ\n", logPrefix);
  servo.write(90);
  delay(1000);
  rotatedFlag = true;
}
