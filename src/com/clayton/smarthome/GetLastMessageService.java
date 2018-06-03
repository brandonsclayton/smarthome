package com.clayton.smarthome;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clayton.smarthome.ArtikCloud.MessageData;
import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.clayton.smarthome.RequestData.LastMessageRequestData;
import com.clayton.smarthome.RequestData.MessageRequestData;

@WebServlet(
    name = "Artik Cloud",
    description = "Artik Cloud connector",
    urlPatterns = {
        "/getLastMessage",
        "/getMessage"
    })
public class GetLastMessageService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVICE_NAME = "Get the last normalized messages from Artik Cloud.";
	
	/**
	 * HTTP servlet for getting last normalized message
	 */
	@Override
	protected void doGet(
	    HttpServletRequest httpRequest, 
	    HttpServletResponse httpResponse) 
	        throws ServletException, IOException {
	  
	  TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
	  try {
      Response svcResponse = processRequest(httpRequest);
      String json = Util.GSON.toJson(svcResponse);
      httpResponse.getWriter().print(json);
	  } catch (Exception e) {
	    httpResponse.getWriter().println(e);
	  }
	}
	
	private static Response processRequest(HttpServletRequest httpRequest) {
	  String servlet = httpRequest.getServletPath();
	  
	  String queryString = httpRequest.getRequestURL()
	      .append("?")
	      .append(httpRequest.getQueryString())
	      .toString();
	      
	  Map<String, String[]> params = httpRequest.getParameterMap();
	  
	  Response.Builder response = Response.builder()
          .status("Success")
          .name(SERVICE_NAME)
          .url(queryString);
	  
	  if (servlet.equals("/getLastMessage")) {
      LastMessageRequestData requestData = new LastMessageRequestData(params);
      MessageReturn messages = ArtikCloud.getLastMessage(
          requestData.deviceGroup,
          requestData.count);
      
      Set<ResponseGroup> responseGroupSet = processTemperature(messages);
      
      response.requestData(requestData)
          .response(responseGroupSet);
	  } else if (servlet.equals("/getMessage")) {
	    MessageRequestData requestData = new MessageRequestData(params);
	    MessageReturn messages = ArtikCloud.getMessage(
	        requestData.deviceGroup,
	        requestData.days, 
	        requestData.hours,
	        requestData.minutes);
	    Set<ResponseGroup> responseGroupSet = processTemperature(messages);
      
	    response.requestData(requestData)
          .response(responseGroupSet);
	  }
	  
	  return response.build(); 
	}
	
	/**
	 * Process request for temperature device group
	 * @param httpRequest
	 * @return
	 */
	private static Set<ResponseGroup> processTemperature(MessageReturn messages) {
	  List<MessageData> temperatureMessage = messages.getDeviceData(Device.TEMPERATURE);
	  Set<DataGroup> dataGroupSet = getTemperatureData(temperatureMessage);
	  
	  List<MessageData> acMessage = messages.getDeviceData(Device.AC);
	  Set<DataGroup> acDataGroupSet = getAcData(acMessage);
	  
	  Set<ResponseGroup> responseGroupSet = new HashSet<>();
	  responseGroupSet.add(
	      new ResponseGroup(Device.TEMPERATURE, dataGroupSet, temperatureMessage));
	  responseGroupSet.add(
	      new ResponseGroup(Device.AC, acDataGroupSet, acMessage));
	  
	  return responseGroupSet;
	}
	
	
	private static Set<DataGroup> getTemperatureData(List<MessageData> temperatureMessage) {
	  
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
	  
	  return dataGroupSet;
	}
	
	private static Set<DataGroup> getAcData(List<MessageData> acMessage) {
    DataGroup ac = DataGroup.builder()
        .display("AC")
        .id("AC")
        .addAll(acMessage, DeviceField.STATE)
        .build();
    
    Set<DataGroup> acDataGroupSet = new HashSet<>();
    acDataGroupSet.add(ac);
	 
    return acDataGroupSet;
	}
	
}
