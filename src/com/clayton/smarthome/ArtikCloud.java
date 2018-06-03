package com.clayton.smarthome;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
  private static final int MAX_COUNT = 1000;
  private static final String ARTIKCLOUD_OAUTH = "artikcloud_oauth";
  private static final ApiClient API_CLIENT = Configuration.getDefaultApiClient();
  private static final OAuth OAUTH = (OAuth) API_CLIENT.getAuthentication(ARTIKCLOUD_OAUTH);
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
      .ofPattern("hh:mm:ss a EEEE, MMMM dd, yyyy");
  
  static {
    API_CLIENT.setDebugging(false);
  }
  
  public static MessageReturn getLastMessage(DeviceGroup deviceGroup, int count) {
    MessagesApi messageApi = new MessagesApi();
    MessageReturn.Builder messageBuilder = MessageReturn.builder();
    count = count > MAX_COUNT ? MAX_COUNT : count;
    
    try {
      for (Device device : deviceGroup) {
        OAUTH.setAccessToken(device.deviceToken);
        
        NormalizedMessagesEnvelope message = messageApi.getLastNormalizedMessages(
            count, /* count */ 
            device.deviceId, /* device id */
            null ); /* field presence */
        
        messageBuilder.put(device, Arrays.asList(message));
        Collections.reverse(messageBuilder.results.get(device));
      }
      
      return messageBuilder.build();
    } catch (ApiException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  public static MessageReturn getMessage(
      DeviceGroup deviceGroup, 
      int days, 
      int hours, 
      int minutes) { 
    MessagesApi messageApi = new MessagesApi();
    MessageReturn.Builder messageBuilder = MessageReturn.builder();
    
    long past = TimeUnit.DAYS.toMillis(days) + 
        TimeUnit.HOURS.toMillis(hours) + 
        TimeUnit.MINUTES.toMillis(minutes);

    long endDate = Instant.now().toEpochMilli();
    long startDate = endDate - past;
    
    try {
      for (Device device : deviceGroup) {
        OAUTH.setAccessToken(device.deviceToken);
        List<NormalizedMessagesEnvelope> normalizedMessages = new ArrayList<>();
        String next = null;
            
        do {
          NormalizedMessagesEnvelope message = messageApi.getNormalizedMessages(
              null, /* uid */
              device.deviceId, /* device id */
              null, /* mid */
              null, /* field presence */
              null, /* filter */
              next, /* offset */
              1000,  /* count */
              startDate, /* start date in milliseconds */ 
              endDate,  /* end date in milliseconds */
              "asc"); /* order (asc || desc) */
          
          next = message.getNext();
          normalizedMessages.add(message);
        } while(next != null);
        
        messageBuilder.put(device, normalizedMessages);
      }
      
      return messageBuilder.build();
    } catch (ApiException e) {
      throw new RuntimeException();
    }
  }
  
  public static class MessageData {
    final long ts;
    final String date;
    final Map<String, JsonElement> data;
    
    public MessageData(long ts, Map<String, Object> data) {
      this.ts = ts;
      this.data = toJsonElement(data);
      
      ZoneId zone = ZoneId.of("America/Denver");
      this.date = Instant.ofEpochMilli(ts).atZone(zone).format(DATE_FMT);
    }
    
    public String toJsonString() {
      return Util.GSON.toJson(this, MessageData.class);
    }
  }
  
  public static class MessageReturn {
    private Map<Device, List<MessageData>> results;
    
    private MessageReturn(Builder builder) {
      this.results = builder.results;
    }

    public List<MessageData> getDeviceData(Device device) {
      return results.get(device);
    }
    
    public Map<Device, List<MessageData>> getData() {
      return results;
    }
    
    public String toJsonString() {
      return Util.GSON.toJson(this, MessageReturn.class);
    }

    private static Builder builder() {
      return new Builder();
    }
    
    private static class Builder {
      private Map<Device, List<MessageData>> results = new HashMap<>();
      
      private MessageReturn build() {
        return new MessageReturn(this);
      }
      
      private Builder put(Device device, List<NormalizedMessagesEnvelope> messages) {
        List<MessageData> data = new ArrayList<>();
        
        for (NormalizedMessagesEnvelope message : messages) {
          message.getData().stream()
              .map(d ->  new MessageData(d.getTs(), d.getData()))
              .forEach(d -> data.add(d));
        }
        
        results.put(device, data);
        return this;
      }
    }
    
  }
  
  private static Map<String, JsonElement> toJsonElement(Map<String, Object> data) {
    return data.entrySet().stream()
        .collect(Collectors.toMap(
            d -> d.getKey(), 
            d -> Util.GSON.toJsonTree(d.getValue())));
  }

}
