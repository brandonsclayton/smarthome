package com.clayton.smarthome;

public enum DeviceField {
  
  AVERAGE_TEMPERATURE("Average Temperature", "Average_Temperature"),
  BEDROOM_TEMPERATURE("Bedroom Temperature", "Bedroom_Temperature"),
  LIVING_ROOM_TEMPERATURE("Living Room Temperature", "Living_Room_Temperature"),
  STATE("State", "state");
  
  String label;
  String id;
  
  DeviceField(String label, String id) {
    this.label = label;
    this.id = id;
  }

}
