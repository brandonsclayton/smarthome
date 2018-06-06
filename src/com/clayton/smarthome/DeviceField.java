package com.clayton.smarthome;

public enum DeviceField {
  
  AVERAGE_TEMPERATURE("Average Temperature", "Average_Temperature"),
  BEDROOM_TEMPERATURE("Average Temperature", "Bedroom_Temperature"),
  LIVING_ROOM_TEMPERATURE("Living Room Temperature", "Living_Room_Temperature"),
  STATE("State", "state");
  
  String display;
  String id;
  
  DeviceField(String display, String id) {
    this.display = display;
    this.id = id;
  }

}
