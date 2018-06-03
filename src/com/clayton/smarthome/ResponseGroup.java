package com.clayton.smarthome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clayton.smarthome.ArtikCloud.MessageData;

public class ResponseGroup {
  String device;
  String deviceLabel;
  String xLabel;
  String yLabel;
  int size;
  Set<DataGroup> dataGroup;
  List<Long> ts;
  List<String> date;
  
  ResponseGroup(Device device, Set<DataGroup> dataGroup, List<MessageData> messageData) {
    this.device = device.toString();
    this.deviceLabel = device.label;
    this.xLabel = device.xLabel;
    this.yLabel = device.yLabel;
    this.size = messageData.size();
    this.dataGroup = dataGroup;
    this.ts = messageData.stream().map(d -> d.ts).collect(Collectors.toList());
    this.date = messageData.stream().map(d -> d.date).collect(Collectors.toList());
  }
}
