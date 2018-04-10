package com.clayton.smarthome;

import java.util.Set;

public class ResponseGroup {
  Device device;
  String deviceId;
  String deviceToken;
  String xLabel;
  String yLabel;
  Set<DataGroup> dataGroup;
  
  ResponseGroup(Device device, Set<DataGroup> dataGroup) {
    this.device = device;
    this.deviceId = device.deviceId;
    this.deviceToken = device.deviceToken;
    this.xLabel = device.xLabel;
    this.yLabel = device.yLabel;
    this.dataGroup = dataGroup;
  }
}
