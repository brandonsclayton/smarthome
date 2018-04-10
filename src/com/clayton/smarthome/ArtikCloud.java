package com.clayton.smarthome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clayton.smarthome.GetLastMessageService.RequestData;

import cloud.artik.api.MessagesApi;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.client.Configuration;
import cloud.artik.client.auth.OAuth;
import cloud.artik.model.NormalizedMessage;
import cloud.artik.model.NormalizedMessagesEnvelope;

public class ArtikCloud {
  private static final String ARTIKCLOUD_OAUTH = "artikcloud_oauth";
  
  static Map<Device, List<NormalizedMessage>> getLastMessage(
      DeviceGroup deviceGroup, 
      int count) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setDebugging(false);
    
    OAuth oauth = (OAuth) defaultClient.getAuthentication(ARTIKCLOUD_OAUTH);
    MessagesApi messageApi = new MessagesApi();
    Map<Device, List<NormalizedMessage>> messages = new HashMap<>();
    try {
      for (Device device : deviceGroup.devices) {
        oauth.setAccessToken(device.deviceToken); 
        NormalizedMessagesEnvelope normalizedMessage = 
            messageApi.getLastNormalizedMessages(
                count,
                device.deviceId,
                null);
        messages.put(device, normalizedMessage.getData());
      }
    } catch (ApiException e) {
      e.printStackTrace();
    }
    
    return messages;
  }

}
