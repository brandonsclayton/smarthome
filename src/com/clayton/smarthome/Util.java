package com.clayton.smarthome;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Util {
static final Gson GSON;
static final DateTimeFormatter DATE_FMT = DateTimeFormatter
    .ofPattern("hh:mm:ss a EEEE, MMMM dd, yyyy");
  
  static {
    GSON = new GsonBuilder()
        .registerTypeAdapter(DeviceGroup.class, new DeviceGroupSerializer())
        .registerTypeAdapter(Device.class, new DeviceSerializer())
        .registerTypeAdapter(DeviceField.class, new DeviceFieldSerializer())
        .disableHtmlEscaping()
        .serializeNulls()
        .setPrettyPrinting()
        .create();
  }
  
  static void setCorsHeadersAndContentType(HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "*");
    response.setHeader("Access-Control-Allow-Headers", "accept,origin,authorization,content-type");
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
  }
  
  private static final class DeviceGroupSerializer implements JsonSerializer<DeviceGroup> {

    @Override
    public JsonElement serialize(
        DeviceGroup deviceGroup, 
        Type typeOfSrc,
        JsonSerializationContext context) {
      
      JsonObject json = new JsonObject();
      json.addProperty(Key.LABEL, deviceGroup.label);
      json.addProperty(Key.ID, deviceGroup.id);
      
      JsonArray jsonArray = new JsonArray();
      
      for (Device device : deviceGroup) {
        jsonArray.add(context.serialize(device, Device.class));
      }
      
      json.add(Key.DEVICES, jsonArray);

      return json;
    }
    
  }
 
  private static final class DeviceSerializer implements JsonSerializer<Device> {

    @Override
    public JsonElement serialize(
        Device device, 
        Type typeOfSrc, 
        JsonSerializationContext context) {
      
      JsonObject json = new JsonObject();
      json.addProperty(Key.LABEL, device.label);
      json.addProperty(Key.ID, device.id);
      json.addProperty(Key.DEVICE_ID, device.deviceId);
      json.addProperty(Key.DEVICE_TOKEN, device.deviceToken);
      json.addProperty(Key.X_LABEL, device.xLabel);
      json.addProperty(Key.Y_LABEL, device.yLabel);
     
      JsonArray jsonArray = new JsonArray();
      for (DeviceField deviceField : device) {
        jsonArray.add(context.serialize(deviceField, DeviceField.class));
      }
      
      json.add(Key.DEVICE_FIELDS, jsonArray);
      
      return json;
    }
    
  }
 
  private static final class DeviceFieldSerializer implements JsonSerializer<DeviceField> {

    @Override
    public JsonElement serialize(
        DeviceField deviceField, 
        Type typeOfSrc,
        JsonSerializationContext context) {
      
      JsonObject json = new JsonObject();
      json.addProperty(Key.LABEL, deviceField.label);
      json.addProperty(Key.ID, deviceField.id);

      return json;
    }
    
  }
  
  static class Key {
    private static final String LABEL = "label";
    private static final String ID = "id";
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_TOKEN = "deviceToken";
    private static final String DEVICES = "devices";
    private static final String X_LABEL = "xLabel";
    private static final String Y_LABEL = "yLabel";
    private static final String DEVICE_FIELDS = "deviceFields";
  }
}
