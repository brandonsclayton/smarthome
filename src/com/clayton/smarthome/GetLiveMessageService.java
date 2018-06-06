package com.clayton.smarthome;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.clayton.smarthome.ArtikCloud.MessageData;
import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.clayton.smarthome.RequestData.LiveMessageRequestData;

import cloud.artik.model.WebSocketError;
import cloud.artik.websocket.FirehoseWebSocket;

@ServerEndpoint(value = "/getLiveMessage")
public class GetLiveMessageService {
  private static final String SERVICE_NAME = "Get live messages from Artik Cloud";
  private static FirehoseWebSocket artikWebSocket;
  
  @OnOpen
  public void onOpen(Session session) throws IOException {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
    
    Map<String, String[]> params = toQueryMap(session.getQueryString());
    LiveMessageRequestData requestData = new LiveMessageRequestData(params);
    
    artikWebSocket = ArtikCloud.getLiveMessage(
        requestData.device, 
        new MessageCallback(requestData, session, ""));
  }
 
  @OnError
  public void onError(Session session, Throwable throwable) throws IOException {
    session.close();
    artikWebSocket.close();
    throwable.printStackTrace();
  }
  
  @OnClose
  public void onClose(Session session) throws IOException {
    session.close();
    artikWebSocket.close(); 
  }
  
  private static Map<String, String[]> toQueryMap(String queryString) {
    List<String> queries = Arrays.stream(queryString.split("&"))
        .collect(Collectors.toList());
    
    Map<String, String[]> queryMap = new HashMap<>();
    
    for (String query : queries) {
      String[] d = query.split("=");
      queryMap.put(d[0], new String[] {d[1]});
    }
    return queryMap;
  }
  
  private static class MessageCallback implements ArtikCloudLiveCallback {
    LiveMessageRequestData requestData;
    Session session;
    String url;
    
    private MessageCallback(LiveMessageRequestData requestData, Session session, String url) {
      this.requestData = requestData;
      this.session = session;
      this.url = url;
    }

    @Override
    public void onMessage(MessageReturn messageReturn) {
      Set<DataGroup> dataGroupSet = new HashSet<>();
      Set<ResponseGroup> responseGroupSet = new HashSet<>();
      List<MessageData> messageData = messageReturn.getDeviceData(requestData.device);
      
      for (DeviceField deviceField : requestData.device.deviceFields) {
        DataGroup dataGroup = DataGroup.builder()
            .display(deviceField.display)
            .id(deviceField.id)
            .addAll(messageData, deviceField)
            .build();
        
        dataGroupSet.add(dataGroup);
      }
      
      responseGroupSet.add(
          new ResponseGroup(requestData.device, dataGroupSet, messageData));
      
      Response response = Response.builder()
          .name(SERVICE_NAME)
          .status("Success")
          .url(url)
          .requestData(requestData)
          .response(responseGroupSet)
          .build();
     
       sendMessage(session, response.toJsonString());
    }

    @Override
    public void onOpen(int httpStatus, String httpStatusMessage) {
      String status = "connecting";
      String message = String.format(
          "Message: [%s], status: [%d] \n", 
          httpStatusMessage, httpStatus);
     
     sendMessage(session, new LiveResponse(status, message).toJsonString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
      String status = "close";
      String message = String.format(
          "Connction closed with [%d][%s][%b]", 
          code, reason, remote); 
      
      sendMessage(session, new LiveResponse(status, message).toJsonString());
    }

    @Override
    public void onError(WebSocketError error) {
      String status = "error";
      String message = String.format("Received error: [%s]", error); 
      
      sendMessage(session, new LiveResponse(status, message).toJsonString());
    }

    @Override
    public void onPing(long timestamp) {
      String status = "ping";
      ZoneId zone = ZoneId.of("America/Denver");
      String date = Instant.ofEpochMilli(timestamp).atZone(zone).format(Util.DATE_FMT);
      String message = String.format("Received ping with ts: [%s]", date);
      
      sendMessage(session, new LiveResponse(status, message).toJsonString());
    }
    
  }
  
  private static void sendMessage(Session session, String message) {
    try {
      session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
 
  private static class LiveResponse {
    private String status;
    private String message;
    
    private LiveResponse(String status, String message) {
      this.status = status;
      this.message = message;
    }
    
    private String toJsonString() {
      return Util.GSON.toJson(this, LiveResponse.class);
    }
  }

}
