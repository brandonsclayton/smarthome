package com.clayton.smarthome;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public enum Device implements Iterable<DeviceField> {
  
  TEMPERATURE(
      "Temperature",
      "temperature",
      "1497d25089db4a8d84997fd5b2a3d65f",
      "17d85311bc7f46519a75a5138c46f221",
      Sets.newHashSet(
          DeviceField.AVERAGE_TEMPERATURE, 
          DeviceField.BEDROOM_TEMPERATURE, 
          DeviceField.LIVING_ROOM_TEMPERATURE),
      "Date",
      "Temperature");
  
  String label;
  String id;
  String deviceId;
  String deviceToken;
  Set<DeviceField> deviceFields;
  String xLabel;
  String yLabel;
  
  private Device(
      String label,
      String id,
      String deviceId, 
      String deviceToken, 
      Set<DeviceField> deviceFields,
      String xLabel, 
      String yLabel) {
    this.label = label;
    this.id = id;
    this.deviceId = deviceId;
    this.deviceToken = deviceToken;
    this.deviceFields = deviceFields;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }

  @Override
  public Iterator<DeviceField> iterator() {
    return deviceFields.iterator();
  }

}
