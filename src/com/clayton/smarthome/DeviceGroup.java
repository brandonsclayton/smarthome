package com.clayton.smarthome;

import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;

public enum DeviceGroup implements Iterable<Device>{

  TEMPERATURE(
      "Temperature Group",
      "temperatureGroup",
      Sets.newHashSet(
          Device.TEMPERATURE,
          Device.AC));
 
  final String label;
  final String id;
  final Set<Device> devices;
  
  private DeviceGroup(String label, String id, Set<Device> devices) {
    this.label = label;
    this.id = id;
    this.devices = devices;
  }

  @Override
  public Iterator<Device> iterator() {
    return devices.iterator();
  }
 
}
