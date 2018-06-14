package com.clayton.smarthome;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.clayton.smarthome.LastMessageResponseGroup;
import com.clayton.smarthome.RequestData.LiveMessageRequestData;

import cloud.artik.model.Acknowledgement;
import cloud.artik.model.ActionOut;
import cloud.artik.model.MessageOut;
import cloud.artik.model.WebSocketError;
import cloud.artik.websocket.ArtikCloudWebSocketCallback;
import cloud.artik.websocket.FirehoseWebSocket;

@ServerEndpoint(value = "/getLiveMessage")
public class GetLiveMessageService {
  private static final String SERVICE_NAME = "Get live messages from Artik Cloud";
 
  private static SessionHandler sessionHandler = new SessionHandler();
  
  @OnOpen
  public void onOpen(Session session) throws IOException {
    System.out.println("Session onOpen: " + session.getId());
    TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
    
    Map<String, List<String>> params = session.getRequestParameterMap();
    LiveMessageRequestData requestData = new LiveMessageRequestData(params);
    
    FirehoseWebSocket artikSession = ArtikCloud.getLiveMessage(
        requestData.device, 
        new MessageCallback(requestData, session, ""));
    
    sessionHandler.addSession(session, artikSession);
  }
 
  @OnError
  public void onError(Session session, Throwable throwable) throws IOException {
    System.out.println("Session onError");
    throwable.printStackTrace();
  }
  
  @OnClose
  public void onClose(Session session) throws IOException {
    System.out.println("Session onClose: " + session.getId());
    
    FirehoseWebSocket artikSession = sessionHandler.getArtikSession(session);
    sessionHandler.removeSession(session);
    artikSession.close();
  }
  
  private static class SessionHandler {
    private Set<Session> sessions = new HashSet<>();
    private Map<Session, FirehoseWebSocket> artikSessions = new HashMap<>();
   
    private void addSession(Session session, FirehoseWebSocket artikSession) {
      sessions.add(session);
      artikSessions.put(session, artikSession);
    }
    
    private void removeSession(Session session) {
      artikSessions.remove(session);
      sessions.remove(session);
    }
   
    private FirehoseWebSocket getArtikSession(Session session) {
      return artikSessions.get(session);
    }
  }
  
  private static class MessageCallback implements ArtikCloudWebSocketCallback {
    LiveMessageRequestData requestData;
    Session session;
    String url;
    
    private MessageCallback(LiveMessageRequestData requestData, Session session, String url) {
      this.requestData = requestData;
      this.session = session;
      this.url = url;
    }

    @Override
    public void onMessage(MessageOut liveMessage) {
      System.out.println("Artik onMessage: " + session.getId());
      MessageReturn messageReturn = MessageReturn.builder()
          .put(requestData.device, liveMessage)
          .build();
      
      LastMessageResponseGroup responseGroup = new LastMessageResponseGroup(messageReturn);
      
      GetLastMessageResponse response = GetLastMessageResponse.builder()
          .name(SERVICE_NAME)
          .status("success")
          .url(url)
          .requestData(requestData)
          .response(responseGroup)
          .build();
     
       sendMessage(session, response.toJsonString());
    }

    @Override
    public void onOpen(int httpStatus, String httpStatusMessage) {
      System.out.println("Artik onOpen: " + session.getId());
      String status = "connecting";
      String message = String.format(
          "Message: [%s], status: [%d] \n", 
          httpStatusMessage, httpStatus);
     
     sendMessage(session, new LiveResponse(status, message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
      System.out.println("Artik onClose");
    }

    @Override
    public void onError(WebSocketError error) {
      System.out.println("Artik onError: " + session.getId());
      String status = "error";
      String message = String.format("Received error: [%s]", error); 
      
      sendMessage(session, new LiveResponse(status, message));
    }

    @Override
    public void onPing(long timestamp) {
      System.out.println("Artik onPing: " + session.getId());
      String status = "ping";
      ZoneId zone = ZoneId.of("America/Denver");
      String date = Instant.ofEpochMilli(timestamp).atZone(zone).format(Util.DATE_FMT);
      String message = String.format("Received ping with ts: [%s]", date);
      
      sendMessage(session, new LiveResponse(status, message));
    }

    @Override
    public void onAction(ActionOut action) {
      
    }

    @Override
    public void onAck(Acknowledgement ack) {
      
    }
    
  }
  
  private static void sendMessage(Session session, String message) {
    try {
      session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static void sendMessage(Session session, LiveResponse liveResponse) {
    try {
      session.getBasicRemote().sendText(liveResponse.toJsonString());
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
