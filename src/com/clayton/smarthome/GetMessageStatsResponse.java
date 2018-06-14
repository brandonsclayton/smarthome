package com.clayton.smarthome;

import java.time.ZonedDateTime;
import java.util.List;

import com.clayton.smarthome.ArtikCloud.AggregateMessageData;

public class GetMessageStatsResponse {
  String name;
  String status;
  String url;
  String date;
  RequestData request;
  List<AggregateMessageData> response;
  
  private GetMessageStatsResponse(Builder builder) {
    this.name = builder.name;
    this.status = builder.status;
    this.url = builder.url;
    this.date = ZonedDateTime.now().format(Util.DATE_FMT);
    this.request = builder.request;
    this.response = builder.response;
  }

  String toJsonString() {
    return Util.GSON.toJson(this, GetMessageStatsResponse.class);
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private  String name;
    private  String status;
    private  String url;
    private  RequestData request;
    private  List<AggregateMessageData> response;
   
    private Builder() {}
   
    GetMessageStatsResponse build() {
      return new GetMessageStatsResponse(this);
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
   
    Builder response(List<AggregateMessageData> data) {
      this.response = data;
      return this;
    }
  }
  
}
