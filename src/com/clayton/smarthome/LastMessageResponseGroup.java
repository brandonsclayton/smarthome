package com.clayton.smarthome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clayton.smarthome.ArtikCloud.MessageData;
import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.clayton.smarthome.LastMessageDataGroup.LastMessageData;

public class LastMessageResponseGroup implements ResponseGroup {
 
  Set<LastMessageResponse> response;
  
  LastMessageResponseGroup(MessageReturn messageReturn) {
    this.response = messageReturn.getDevices().stream()
        .map(device -> new LastMessageResponse(device, messageReturn.getDeviceData(device)))
        .collect(Collectors.toSet());
  }
  
  static class LastMessageResponse {
    Device device;
    int size;
    Set<LastMessageData> dataGroup;
    List<Long> ts;
    List<String> date;
    
    LastMessageResponse(Device device, List<MessageData> messageData) {
      this.device = device;
      this.size = messageData.size();
      this.dataGroup = new LastMessageDataGroup(messageData, device).data;
      this.ts = messageData.stream().map(d -> d.ts).collect(Collectors.toList());
      this.date = messageData.stream().map(d -> d.date).collect(Collectors.toList());
    }
  }
  
}
