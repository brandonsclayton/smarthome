package com.clayton.smarthome;

public enum Device {
  
  AC(
      "AC",
      "22190f84bb0845e5a571ab17269f88a4",
      "6519448711e0448db33b8b17565cebd9",
      "Date",
      "Status"),
  
  TEMPERATURE(
      "Temperature",
      "1497d25089db4a8d84997fd5b2a3d65f",
      "17d85311bc7f46519a75a5138c46f221",
      "Date",
      "Temperature");
  
  String label;
  String deviceId;
  String deviceToken;
  String xLabel;
  String yLabel;
  
  private Device(String label ,String deviceId, String deviceToken, String xLabel, String yLabel) {
    this.label = label;
    this.deviceId = deviceId;
    this.deviceToken = deviceToken;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
  }

}
