package com.clayton.smarthome;

import java.util.ArrayList;
import java.util.List;

import com.clayton.smarthome.ArtikCloud.MessageData;

import com.google.gson.JsonElement;

public class DataGroup {
  String id;
  String display;
  List<JsonElement> data; 
  
  DataGroup(Builder builder) {
   this.data = builder.data;
   this.id = builder.id;
   this.display = builder.display;
  }
  
  static Builder builder() {
    return new Builder();
  }
  
  static class Builder {
    private String id;
    private String display;
    private List<JsonElement> data = new ArrayList<>();
    
    Builder() {}
    
    DataGroup build() {
      return new DataGroup(this);
    }
    
    Builder add(JsonElement data) {
      this.data.add(data);
      return this;
    }
    
    Builder addAll(List<MessageData> data, DeviceField deviceField) {
      for (MessageData messageData : data) {
        this.data.add(messageData.data.get(deviceField.id));
      }
      return this;
    }
    
    Builder id(String id) {
      this.id = id;
      return this;
    }
    
    Builder display(String display) {
      this.display = display;
      return this;
    }
    
  }
  

}
