package com.clayton.smarthome;

import java.util.Set;
import com.google.common.collect.Sets;

public enum DeviceGroup {

  TEMPERATURE(
      Sets.newHashSet(
          Device.TEMPERATURE));
  
  Set<Device> devices;
  
  private DeviceGroup(Set<Device> devices) {
    this.devices = devices;
  }

 
}
