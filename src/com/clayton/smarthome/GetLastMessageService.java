package com.clayton.smarthome;

import java.util.Map;
import java.util.TimeZone;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.clayton.smarthome.LastMessageResponseGroup;
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
	 
	  Util.setCorsHeadersAndContentType(httpResponse);
	  TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
	  try {
      GetLastMessageResponse svcResponse = processRequest(httpRequest);
      String json = svcResponse.toJsonString();
      httpResponse.getWriter().print(json);
	  } catch (Exception e) {
	    httpResponse.getWriter().println(e);
	  }
	}
	
	private static GetLastMessageResponse processRequest(HttpServletRequest httpRequest) {
	  String servlet = httpRequest.getServletPath();
	  
	  String queryString = httpRequest.getRequestURL()
	      .append("?")
	      .append(httpRequest.getQueryString())
	      .toString();
	      
	  Map<String, String[]> params = httpRequest.getParameterMap();
	  
	  GetLastMessageResponse.Builder response = GetLastMessageResponse.builder()
          .status("Success")
          .name(SERVICE_NAME)
          .url(queryString);
	  
	  if ("/getLastMessage".equals(servlet)) {
      LastMessageRequestData requestData = new LastMessageRequestData(params);
      
      MessageReturn messageReturn = ArtikCloud.getLastMessage(
          requestData.deviceGroup,
          requestData.count);
      
      LastMessageResponseGroup responseGroup = new LastMessageResponseGroup(messageReturn); 
      
      response.requestData(requestData)
          .response(responseGroup);
	  } else if ("/getMessage".equals(servlet)) {
	    MessageRequestData requestData = new MessageRequestData(params);
	    
	    MessageReturn messageReturn = ArtikCloud.getMessage(
	        requestData.deviceGroup,
	        requestData.days, 
	        requestData.hours,
	        requestData.minutes);
	    
      LastMessageResponseGroup responseGroup = new LastMessageResponseGroup(messageReturn); 
      
	    response.requestData(requestData)
          .response(responseGroup);
	  } 
	  
	  return response.build(); 
	}
	
}
