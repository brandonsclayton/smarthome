package com.clayton.smarthome;

import java.util.ArrayList;
import java.util.Date;

public class DataGroup {
  String id;
  String display;
  ArrayList<Object[]> data = new ArrayList<>();
  
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
    private ArrayList<Object[]> data = new ArrayList<>();
    
    Builder() {}
    
    DataGroup build() {
      return new DataGroup(this);
    }
    
    Builder add(Date date, Object value) {
      this.data.add(new Object[] {date, value});
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
