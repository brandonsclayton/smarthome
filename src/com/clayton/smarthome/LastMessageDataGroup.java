package com.clayton.smarthome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clayton.smarthome.ArtikCloud.MessageData;

import com.google.gson.JsonElement;

public class LastMessageDataGroup {
  
  Set<LastMessageData> data;
  
  LastMessageDataGroup(List<MessageData> messageData, Device device) {
    this.data = device.deviceFields.stream()
        .map(deviceField -> new LastMessageData(messageData, deviceField))
        .collect(Collectors.toSet());
  }
  
  static class LastMessageData {
    DeviceField deviceField;
    List<JsonElement> data; 
   
    LastMessageData(List<MessageData> messageData, DeviceField deviceField) {
      this.deviceField = deviceField;
      this.data = messageData.stream()
          .map(d -> d.data.get(deviceField.id))
          .collect(Collectors.toList());
    }
  }

}
