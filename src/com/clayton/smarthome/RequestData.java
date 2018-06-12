package com.clayton.smarthome;

import java.util.List;
import java.util.Map;

public class RequestData {
  
  private RequestData() {}
  
  static class LastMessageRequestData extends RequestData {
    DeviceGroup deviceGroup;
    int count;
    
    LastMessageRequestData(Map<String, String[]> httpParams) {
      this.deviceGroup = DeviceGroup.valueOf(httpParams.get(Key.DEVICE_GROUP)[0].toUpperCase());
      String count = httpParams.get(Key.COUNT)[0]; 
      this.count = count == null ? 1 : Integer.parseInt(count); 
    }
  }
  
  static class MessageRequestData extends RequestData {
    DeviceGroup deviceGroup;
    int days;
    int hours;
    int minutes;
    
    MessageRequestData(Map<String, String[]> httpParams) {
      this.deviceGroup = DeviceGroup.valueOf(httpParams.get(Key.DEVICE_GROUP)[0].toUpperCase());
      String[] days = httpParams.get(Key.DAYS);
      String[] hours = httpParams.get(Key.HOURS);
      String[] minutes = httpParams.get(Key.MINUTES);
      
      this.days = days == null ? 0 : Integer.parseInt(days[0]);
      this.hours = hours == null ? 0 : Integer.parseInt(hours[0]); 
      this.minutes = minutes == null ? 0 : Integer.parseInt(minutes[0]); 
    }
  }
  
  static class LiveMessageRequestData extends RequestData {
    Device device;
    
    LiveMessageRequestData(Map<String, List<String>> httpParams) {
      this.device = Device.valueOf(httpParams.get(Key.DEVICE).get(0).toUpperCase());
    }
  }
  
  static class MessageStatsRequestData extends RequestData {
    Device device;
    
    MessageStatsRequestData(Map<String, String[]> httpParams) {
      this.device = Device.valueOf(httpParams.get(Key.DEVICE)[0].toUpperCase());
    }
  }
  
  private static class Key {
    private static final String COUNT = "count";
    private static final String DEVICE = "device";
    private static final String DEVICE_GROUP = "devicegroup";
    private static final String DAYS = "days";
    private static final String MINUTES = "minutes";
    private static final String HOURS = "hours";
  }
  

}
