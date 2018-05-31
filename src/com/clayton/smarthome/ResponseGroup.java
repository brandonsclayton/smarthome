package com.clayton.smarthome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clayton.smarthome.ArtikCloud.MessageData;

public class ResponseGroup {
  Device device;
  String deviceId;
  String deviceToken;
  String xLabel;
  String yLabel;
  List<Long> ts;
  List<String> date;
  Set<DataGroup> dataGroup;
  
  ResponseGroup(Device device, Set<DataGroup> dataGroup, List<MessageData> messageData) {
    this.device = device;
    this.deviceId = device.deviceId;
    this.deviceToken = device.deviceToken;
    this.xLabel = device.xLabel;
    this.yLabel = device.yLabel;
    this.ts = messageData.stream().map(d -> d.ts).collect(Collectors.toList());
    this.date = messageData.stream().map(d -> d.date).collect(Collectors.toList());
    this.dataGroup = dataGroup;
  }
}
