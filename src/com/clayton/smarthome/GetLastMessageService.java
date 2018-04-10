package com.clayton.smarthome;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	static Gson gson;
	
	static {
	  gson = new GsonBuilder()
	      .setPrettyPrinting()
	      .create();
	}
	
	/**
	 * HTTP servlet for getting last normalized message
	 */
	@Override
	protected void doGet(
	    HttpServletRequest httpRequest, 
	    HttpServletResponse httpResponse) 
	        throws ServletException, IOException {
	  
	  Response svcResponse = processRequestTemperature(httpRequest);
	  String json = gson.toJson(svcResponse);
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
	  Map<Device, List<NormalizedMessage>> messages = ArtikCloud.getLastMessage(
	      requestData.deviceGroup,
	      requestData.count);
	  
	  List<NormalizedMessage> temperatureMessage = messages.get(Device.TEMPERATURE);
	  DataGroup.Builder avgTemp = DataGroup.builder()
	      .display("Average Temperature")
	      .id("Average_Temperature");
	  DataGroup.Builder bedTemp = DataGroup.builder()
	      .display("Bedroom Temperature")
	      .id("Bedroom_Temperature");
	  DataGroup.Builder livTemp = DataGroup.builder()
	      .display("Living Room Temperature")
	      .id("Living_Room_Temperature");
	  
	  for (NormalizedMessage message : temperatureMessage) {
	    Map<String, Object> messageData = message.getData();
	    Long ts = message.getTs();
	    Date date = new Date(ts);
	    
	    avgTemp.add(date, (Double) messageData.get("Average_Temperature"));
	    bedTemp.add(date, (Double) messageData.get("Bedroom_Temperature"));
	    livTemp.add(date, (Double) messageData.get("Living_Room_Temperature"));
	  }
	  
	  Set<DataGroup> dataGroupSet = new HashSet<>();
	  dataGroupSet.add(avgTemp.build());
	  dataGroupSet.add(bedTemp.build());
	  dataGroupSet.add(livTemp.build());
	  
	  Set<ResponseGroup> responseGroupSet = new HashSet<>();
	  responseGroupSet.add(new ResponseGroup(Device.TEMPERATURE, dataGroupSet));
	  
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
