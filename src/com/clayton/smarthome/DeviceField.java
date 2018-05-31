package com.clayton.smarthome;

public enum DeviceField {
  
  AVERAGE_TEMPERATURE("Average_Temperature"),
  BEDROOM_TEMPERATURE("Bedroom_Temperature"),
  LIVING_ROOM_TEMPERATURE("Living_Room_Temperature"),
  STATE("state");
  
  String id;
  
  DeviceField(String id) {
    this.id = id;
  }

}
