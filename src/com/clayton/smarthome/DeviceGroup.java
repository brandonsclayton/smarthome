package com.clayton.smarthome;

import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;

public enum DeviceGroup implements Iterable<Device>{

  TEMPERATURE(
      Sets.newHashSet(
          Device.TEMPERATURE,
          Device.AC));
  
  Set<Device> devices;
  
  private DeviceGroup(Set<Device> devices) {
    this.devices = devices;
  }

  @Override
  public Iterator<Device> iterator() {
    return devices.iterator();
  }
 
}
