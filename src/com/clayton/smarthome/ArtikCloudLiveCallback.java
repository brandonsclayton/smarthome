package com.clayton.smarthome;

import java.io.IOException;

import com.clayton.smarthome.ArtikCloud.MessageReturn;

import cloud.artik.model.WebSocketError;

public interface ArtikCloudLiveCallback {
  
  public void onMessage(MessageReturn messageReturn); 
  
  public void onOpen(int httpStatus, String httpStatusMessage);

  public void onClose(int code, String reason, boolean remote);

  public void onError(WebSocketError error);
  
  public void onPing(long timestamp);

}
