package com.clayton.smarthome;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import cloud.artik.model.Acknowledgement;
import cloud.artik.model.ActionOut;
import cloud.artik.model.AggregateData;
import cloud.artik.model.AggregatesResponse;
import cloud.artik.model.MessageOut;
import cloud.artik.model.NormalizedMessagesEnvelope;
import cloud.artik.model.WebSocketError;
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
  
  public static AggregateMessageReturn getMessageStats(Device device) {
    MessagesApi apiInstance = new MessagesApi();
    Long endDate = Instant.now().toEpochMilli(); 
    Long startDate =  endDate - TimeUnit.DAYS.toMillis(1);
    
    OAUTH.setAccessToken(device.deviceToken);
    String sdid = device.deviceId; 
    AggregateMessageReturn.Builder messageReturn = AggregateMessageReturn.builder();
    try {
      for (DeviceField deviceField : device.deviceFields) {
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
  
  public static FirehoseWebSocket getLiveMessage(Device device, ArtikCloudLiveCallback callback) {
    try {
      FirehoseWebSocket ws = new FirehoseWebSocket(
          device.deviceToken,
          device.deviceId,
          null,
          null,
          null,
          new LiveCallback(device, callback));
      
      ws.connect();
      
      return ws;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("ArtikCloud.getLiveMessage Error: " + e.getMessage());
    }
  }
  

  private static class LiveCallback implements ArtikCloudWebSocketCallback {
    Device device;
    ArtikCloudLiveCallback callback;
    
    private LiveCallback(Device device, ArtikCloudLiveCallback callback) {
      this.device = device;
      this.callback = callback;
    }
    
    @Override
    public void onOpen(int httpStatus, String httpStatusMessage) {
      this.callback.onOpen(httpStatus, httpStatusMessage);
    }

    @Override
    public void onMessage(MessageOut liveMessage) {
      MessageReturn results = MessageReturn.builder()
          .put(device, liveMessage)
          .build();
      this.callback.onMessage(results);
    }

    @Override
    public void onAction(ActionOut action) {
      System.out.println(String.format("Received action:[%s]", action));
    }

    @Override
    public void onAck(Acknowledgement ack) {
      System.out.println(String.format("Received Ack [%s]", ack));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
      this.callback.onClose(code, reason, remote);
    }

    @Override
    public void onError(WebSocketError error) {
      this.callback.onError(error);
    }

    @Override
    public void onPing(long timestamp) {
      this.callback.onPing(timestamp);
    }
  
  }
  
  public static class AggregateMessageData {
    String deviceField;
    Long count;
    double max;
    double min;
    double mean;
    
    AggregateMessageData(DeviceField deviceField, AggregateData response) {
      this.deviceField = deviceField.id;
      this.count = response.getCount();
      this.max = Math.round(response.getMax() * 100.0) / 100.0;
      this.min = Math.round(response.getMin() * 100.0) / 100.0;
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
      
      private Builder put(Device device, MessageOut liveMessage) {
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
