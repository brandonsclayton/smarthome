package com.clayton.smarthome;

import java.util.Set;

import com.clayton.smarthome.GetLastMessageService.RequestData;

public class Response {
  String name;
  String status;
  String url;
  RequestData request;
  Set<ResponseGroup> response;
  
  private Response(Builder builder) {
    this.name = builder.name;
    this.status = builder.status;
    this.url = builder.url;
    this.request = builder.request;
    this.response = builder.response;
  }
  
  static Builder builder() {
    return new Builder();
  }
  
  static class Builder {
    private String name;
    private String status;
    private String url;
    private RequestData request;
    private Set<ResponseGroup> response;
    
    private Builder() {}
  
    Response build() {
      return new Response(this);
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
    
    Builder response(Set<ResponseGroup> response) {
      this.response = response;
      return this;
    }
    
  }
}
