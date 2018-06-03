package com.clayton.smarthome;

import java.util.Map;

public class RequestData {
  DeviceGroup deviceGroup;
  
  private RequestData() {}
  
  static class LastMessageRequestData extends RequestData {
    int count;
    
    LastMessageRequestData(Map<String, String[]> httpParams) {
      this.deviceGroup = DeviceGroup.valueOf(httpParams.get("devicegroup")[0].toUpperCase());
      String count = httpParams.get("count")[0]; 
      this.count = count == null ? 1 : Integer.parseInt(count); 
    }
  }
  
  static class MessageRequestData extends RequestData {
    int days;
    int hours;
    int minutes;
    
    MessageRequestData(Map<String, String[]> httpParams) {
      this.deviceGroup = DeviceGroup.valueOf(httpParams.get("devicegroup")[0].toUpperCase());
      String[] days = httpParams.get("days");
      String[] hours = httpParams.get("hours");
      String[] minutes = httpParams.get("minutes");
      
      this.days = days == null ? 0 : Integer.parseInt(days[0]);
      this.hours = hours == null ? 0 : Integer.parseInt(hours[0]); 
      this.minutes = minutes == null ? 0 : Integer.parseInt(minutes[0]); 
    }
  }

}
