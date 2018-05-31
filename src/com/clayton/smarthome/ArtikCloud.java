package com.clayton.smarthome;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import cloud.artik.api.MessagesApi;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.client.Configuration;
import cloud.artik.client.auth.OAuth;
import cloud.artik.model.NormalizedMessagesEnvelope;

public class ArtikCloud {
  private static final String ARTIKCLOUD_OAUTH = "artikcloud_oauth";
  private static final ApiClient API_CLIENT = Configuration.getDefaultApiClient();
  private static final OAuth OAUTH = (OAuth) API_CLIENT.getAuthentication(ARTIKCLOUD_OAUTH);
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
      .ofPattern("hh:mm:ss a EEEE, MMMM dd, yyyy");
  
  static {
    API_CLIENT.setDebugging(true);
  }
  
  static MessageReturn getLastMessage(DeviceGroup deviceGroup, int count) {
    MessagesApi messageApi = new MessagesApi();
    MessageReturn.Builder messageBuilder = MessageReturn.builder();
    
    
    try {
      for (Device device : deviceGroup) {
        OAUTH.setAccessToken(device.deviceToken);
        NormalizedMessagesEnvelope normalizedMessage = messageApi
          .getLastNormalizedMessages(
                count, 
                device.deviceId ,
                null );

        messageBuilder.put(device, normalizedMessage);
        Collections.reverse(messageBuilder.results.get(device));
      }
      
      return messageBuilder.build();
    } catch (ApiException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  static MessageReturn getMessage(DeviceGroup deviceGroup, long startDate, long endDate) {
    MessagesApi messageApi = new MessagesApi();
    MessageReturn.Builder messageBuilder = MessageReturn.builder();
    
    try {
      for (Device device : deviceGroup) {
        OAUTH.setAccessToken(device.deviceToken);
        
        NormalizedMessagesEnvelope normalizedMessage = messageApi
            .getNormalizedMessages(
                null, /* uid */
                device.deviceId, /* device id */
                null, /* mid */
                null, /* field presence */
                null, /* filter */
                null, /* offset */
                null,  /* count */
                startDate, /* start date in milliseconds */ 
                endDate,  /* end date in milliseconds */
                "asc"); /* order (asc || desc) */
        
        messageBuilder.put(device, normalizedMessage);
      }
      
      return messageBuilder.build();
    } catch (ApiException e) {
      throw new RuntimeException();
    }
  }
  
  static class MessageData {
    final long ts;
    final String date;
    final Map<String, JsonElement> data;
    
    MessageData(long ts, Map<String, Object> data) {
      this.ts = ts;
      this.data = toJsonElement(data);
      
      ZoneId zone = ZoneId.of("America/Denver");
      this.date = Instant.ofEpochMilli(ts).atZone(zone).format(DATE_FMT);
    }
    
    String toJsonString() {
      return Util.GSON.toJson(this, MessageData.class);
    }
  }
  
  private static Map<String, JsonElement> toJsonElement(Map<String, Object> data) {
    return data.entrySet().stream()
        .collect(Collectors.toMap(
            d -> d.getKey(), 
            d -> Util.GSON.toJsonTree(d.getValue())));
  }

  /*
  static class MessageData2 {
    List<Long> ts;
    List<String> date;
    List<Map<String, JsonElement>> data;
    
    static class Builder {
      List<Long> ts = new ArrayList<>();
      List<String> date = new ArrayList<>();
      List<Map<String, JsonElement>> data = new ArrayList<>();
      ZoneId zone = ZoneId.of("America/Denver");
      
      Builder add(long ts, Map<String, Object> data) {
        this.ts.add(ts);
        this.date.add(Instant.ofEpochMilli(ts).atZone(zone).format(DATE_FMT));
        this.data.add(toJsonElement(data));
        return this;
      }
    }
  }
  */
  
  static class MessageReturn {
    private Map<Device, List<MessageData>> results;
    
    private MessageReturn(Builder builder) {
      this.results = builder.results;
    }

    private static Builder builder() {
      return new Builder();
    }
    
    private static class Builder {
      private Map<Device, List<MessageData>> results = new HashMap<>();
      
      private MessageReturn build() {
        return new MessageReturn(this);
      }
      
      private Builder put(Device device, NormalizedMessagesEnvelope message) {
        List<MessageData> data = message.getData().stream()
            .map(d -> new MessageData(d.getTs(), d.getData()))
            .collect(Collectors.toList());
        results.put(device, data);
        return this;
      }
    }
    
    List<MessageData> getDeviceData(Device device) {
      return results.get(device);
    }
    
    Map<Device, List<MessageData>> getData() {
      return results;
    }
    
    String toJsonString() {
      return Util.GSON.toJson(this, MessageReturn.class);
    }
  }
  
}
