package com.clayton.smarthome;

import java.util.Set;

import com.clayton.smarthome.LastMessageResponseGroup.LastMessageResponse;
import com.clayton.smarthome.RequestData;

public class GetLastMessageResponse {
  String name;
  String status;
  String url;
  RequestData request;
  Set<LastMessageResponse> response;
  
  private GetLastMessageResponse(Builder builder) {
    this.name = builder.name;
    this.status = builder.status;
    this.url = builder.url;
    this.request = builder.request;
    this.response = builder.response;
  }
  
  public String toJsonString() {
    return Util.GSON.toJson(this, GetLastMessageResponse.class);
  }

  static Builder builder() {
    return new Builder();
  }
  
  static class Builder {
    private String name;
    private String status;
    private String url;
    private RequestData request;
    private Set<LastMessageResponse> response;
    
    private Builder() {}
  
    GetLastMessageResponse build() {
      return new GetLastMessageResponse(this);
    }
    
    Builder name(String name) {
      this.name = name;
      return this;
    }
    
    Builder status(String status) {
      this.status = status;
      return this;
    }
    
    Builder url(String url) {
      this.url = url;
      return this;
    }
    
    Builder requestData(RequestData request) {
      this.request = request;
      return this;
    }
    
    Builder response(LastMessageResponseGroup responseGroup) {
      this.response = responseGroup.response;
      return this;
    }
    
  }
  
}
