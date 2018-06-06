package com.clayton.smarthome;

import java.util.Set;

import com.google.common.collect.Sets;

public enum Device {
  
  AC(
      "AC",
      "22190f84bb0845e5a571ab17269f88a4",
      "6519448711e0448db33b8b17565cebd9",
      Sets.newHashSet(DeviceField.STATE),
      "Date",
      "Status"),
  
  TEMPERATURE(
      "Temperature",
      "1497d25089db4a8d84997fd5b2a3d65f",
      "17d85311bc7f46519a75a5138c46f221",
      Sets.newHashSet(
          DeviceField.AVERAGE_TEMPERATURE, 
          DeviceField.BEDROOM_TEMPERATURE, 
          DeviceField.LIVING_ROOM_TEMPERATURE),
      "Date",
      "Temperature");
  
  String label;
  String deviceId;
  String deviceToken;
  Set<DeviceField> deviceFields;
  String xLabel;
  String yLabel;
  
  private Device(
      String label,
      String deviceId, 
      String deviceToken, 
      Set<DeviceField> deviceFields,
      String xLabel, 
      String yLabel) {
    this.label = label;
    this.deviceId = deviceId;
    this.deviceToken = deviceToken;
    this.deviceFields = deviceFields;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }

}
