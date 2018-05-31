package com.clayton.smarthome;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beust.jcommander.internal.Sets;
import com.clayton.smarthome.ArtikCloud.MessageData;
import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cloud.artik.api.MessagesApi;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.client.Configuration;
import cloud.artik.client.auth.OAuth;
import cloud.artik.model.NormalizedMessage;
import cloud.artik.model.NormalizedMessagesEnvelope;

@WebServlet(
    name = "Artik Cloud",
    description = "Artik Cloud connector",
    urlPatterns = {
        "/getLastMessage"
    })
public class GetLastMessageService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final String SERVICE_NAME = "Get the last normalized messages from Artik Cloud.";
	
	/**
	 * HTTP servlet for getting last normalized message
	 */
	@Override
	protected void doGet(
	    HttpServletRequest httpRequest, 
	    HttpServletResponse httpResponse) 
	        throws ServletException, IOException {
	  
	  TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
	  Response svcResponse = processRequestTemperature(httpRequest);
	  String json = Util.GSON.toJson(svcResponse);
		httpResponse.getWriter().print(json);
	}
	
	/**
	 * Process request for temperature device group
	 * @param httpRequest
	 * @return
	 */
	Response processRequestTemperature(HttpServletRequest httpRequest) {
	  String queryString = httpRequest.getRequestURL()
	      .append("?")
	      .append(httpRequest.getQueryString())
	      .toString();
	      
	  Map<String, String[]> params = httpRequest.getParameterMap();
	  RequestData requestData = new RequestData(params);
	  MessageReturn messages = ArtikCloud.getLastMessage(
	      requestData.deviceGroup,
	      requestData.count);
	  
	  List<MessageData> temperatureMessage = messages.getDeviceData(Device.TEMPERATURE);
	  
	  DataGroup avgTemp = DataGroup.builder()
	      .display("Average Temperature")
	      .id("Average_Temperature")
	      .addAll(temperatureMessage, DeviceField.AVERAGE_TEMPERATURE)
	      .build();
	      
	  DataGroup bedTemp = DataGroup.builder()
	      .display("Bedroom Temperature")
	      .id("Bedroom_Temperature")
	      .addAll(temperatureMessage, DeviceField.BEDROOM_TEMPERATURE)
	      .build();
	  
	  DataGroup livTemp = DataGroup.builder()
	      .display("Living Room Temperature")
	      .id("Living_Room_Temperature")
	      .addAll(temperatureMessage, DeviceField.LIVING_ROOM_TEMPERATURE)
	      .build();
	  
	  Set<DataGroup> dataGroupSet = new HashSet<>();
	  dataGroupSet.add(avgTemp);
	  dataGroupSet.add(bedTemp);
	  dataGroupSet.add(livTemp);
	  
	  List<MessageData> acMessage = messages.getDeviceData(Device.AC);
    
    DataGroup ac = DataGroup.builder()
        .display("AC")
        .id("AC")
        .addAll(acMessage, DeviceField.STATE)
        .build();
    
    Set<DataGroup> acDataGroupSet = new HashSet<>();
    acDataGroupSet.add(ac);
	  
	  Set<ResponseGroup> responseGroupSet = new HashSet<>();
	  responseGroupSet.add(
	      new ResponseGroup(Device.TEMPERATURE, dataGroupSet, temperatureMessage));
	  responseGroupSet.add(
	      new ResponseGroup(Device.AC, acDataGroupSet, acMessage));
	  
	  Response response = Response.builder()
	      .name(SERVICE_NAME)
	      .requestData(requestData)
	      .response(responseGroupSet)
	      .status("Success")
	      .url(queryString)
	      .build();
	  
	  return response;
	}
	
	static class RequestData {
	  int count;
	  DeviceGroup deviceGroup;
	  
	  RequestData(Map<String, String[]> params) {
	    this.deviceGroup = DeviceGroup.valueOf(params.get("devicegroup")[0].toUpperCase());
	    this.count = Integer.parseInt(params.get("count")[0]);
	  }
	}
	
}
