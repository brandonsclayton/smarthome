package com.clayton.smarthome;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import cloud.artik.api.MessagesApi;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.client.Configuration;
import cloud.artik.client.auth.OAuth;
import cloud.artik.model.AggregateData;
import cloud.artik.model.AggregatesResponse;
import cloud.artik.model.Message;
import cloud.artik.model.MessageIDEnvelope;
import cloud.artik.model.MessageOut;
import cloud.artik.model.NormalizedMessagesEnvelope;
import cloud.artik.websocket.ArtikCloudWebSocketCallback;
import cloud.artik.websocket.FirehoseWebSocket;

public class ArtikCloud {
  private static final int MAX_COUNT = 1000;
  private static final String ARTIKCLOUD_OAUTH = "artikcloud_oauth";
  private static final ApiClient API_CLIENT = Configuration.getDefaultApiClient();
  private static final OAuth OAUTH = (OAuth) API_CLIENT.getAuthentication(ARTIKCLOUD_OAUTH);
  
  static {
    API_CLIENT.setDebugging(false);
  }
 
  public static MessageIDEnvelope postMessage(Device device, Map<String, Object> messageData) {
    OAUTH.setAccessToken(device.deviceToken);
    MessagesApi messageApi = new MessagesApi();
    Message message = new Message();
    message.setSdid(device.deviceId);
    message.setData(messageData);
    
    try {
      return messageApi.sendMessage(message);
    } catch (ApiException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
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
            null); /* field presence */
        
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
 
  public static AggregateMessageReturn getMessageStats(
      Device device, 
      long startDate, 
      long endDate) {
    
    MessagesApi apiInstance = new MessagesApi();
    OAUTH.setAccessToken(device.deviceToken);
    String sdid = device.deviceId; 
    AggregateMessageReturn.Builder messageReturn = AggregateMessageReturn.builder();
    
    try {
      for (DeviceField deviceField : device) {
        AggregatesResponse response = apiInstance.getMessageAggregates(
            sdid, /* Device id */
            deviceField.id,  /* data field */
            startDate, /* start date */
            endDate); /* end date */
       
        messageReturn.add(deviceField, response.getData().get(0));
      }
      
      return messageReturn.build();
    } catch (ApiException e) {
      throw new RuntimeException();
    } 
  }
  
  public static AggregateMessageReturn getMessageStats(
      Device device,
      int days,
      int hours,
      int minutes) {
    
    long past = TimeUnit.DAYS.toMillis(days) + 
        TimeUnit.HOURS.toMillis(hours) + 
        TimeUnit.MINUTES.toMillis(minutes);

    long endDate = Instant.now().toEpochMilli();
    long startDate = endDate - past;
    
    return getMessageStats(device, startDate, endDate);
  }
  
  public static FirehoseWebSocket getLiveMessage(
      Device device, 
      ArtikCloudWebSocketCallback callback) {

    try {
      FirehoseWebSocket ws = new FirehoseWebSocket(
          device.deviceToken,
          device.deviceId,
          null,
          null,
          null,
          callback);
      
      ws.connect();
      
      return ws;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("ArtikCloud.getLiveMessage Error: " + e.getMessage());
    }
  }
  
  public static class AggregateMessageData {
    DeviceField deviceField;
    Long count;
    double min;
    double max;
    double mean;
    
    AggregateMessageData(DeviceField deviceField, AggregateData response) {
      this.deviceField = deviceField;
      this.count = response.getCount();
      this.min = Math.round(response.getMin() * 100.0) / 100.0;
      this.max = Math.round(response.getMax() * 100.0) / 100.0;
      this.mean = Math.round(response.getMean() * 100.0) / 100.0;
    }
    
    public String toJsonString() {
      return Util.GSON.toJson(this, AggregateMessageData.class);
    }

  }
  
  public static class AggregateMessageReturn implements Iterable<AggregateMessageData> {
    private List<AggregateMessageData> results;
    
    private AggregateMessageReturn(Builder builder) {
      this.results = builder.results;
    }
    
    public List<AggregateMessageData> getData() {
      return results;
    }
    
    public AggregateMessageData getDeviceFieldData(DeviceField deviceField) {
      return results.stream()
          .filter(data -> data.deviceField.id == deviceField.id)
          .findFirst()
          .orElse(null);
    }
   
    @Override
    public Iterator<AggregateMessageData> iterator() {
      return results.iterator();
    }
    
    public String toJsonString() {
      return Util.GSON.toJson(this, AggregateMessageReturn.class);
    }

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private List<AggregateMessageData> results = new ArrayList<>();
      
      private Builder() {}
     
      public AggregateMessageReturn build() {
        return new AggregateMessageReturn(this);
      }
      
      public Builder add(DeviceField deviceField, AggregateData response) {
        AggregateMessageData data = new AggregateMessageData(deviceField, response);
        this.results.add(data);
        return this;
      }
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
      this.date = Instant.ofEpochMilli(ts).atZone(zone).format(Util.DATE_FMT);
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
   
    public Set<Device> getDevices() {
      return results.keySet();
    }
    
    public String toJsonString() {
      return Util.GSON.toJson(this, MessageReturn.class);
    }

    static Builder builder() {
      return new Builder();
    }
    
    static class Builder {
      private Map<Device, List<MessageData>> results = new HashMap<>();
      
      MessageReturn build() {
        return new MessageReturn(this);
      }
      
      Builder put(Device device, List<NormalizedMessagesEnvelope> messages) {
        List<MessageData> data = new ArrayList<>();
        
        for (NormalizedMessagesEnvelope message : messages) {
          message.getData().stream()
              .map(d ->  new MessageData(d.getTs(), d.getData()))
              .forEach(d -> data.add(d));
        }
        
        results.put(device, data);
        return this;
      }
      
      Builder put(Device device, MessageOut liveMessage) {
        List<MessageData> data = Arrays.asList(
            new MessageData(liveMessage.getTs(), liveMessage.getData()));
        
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
