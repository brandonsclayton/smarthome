package com.clayton.smarthome;

import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clayton.smarthome.ArtikCloud.AggregateMessageReturn;
import com.clayton.smarthome.RequestData.MessageStatsRequestData;

@WebServlet(
    name = "Artik Cloud Aggregate",
    description = "Artik Cloud aggregate connector",
    urlPatterns = {
      "/getLastMessageStats"})
public class GetAggregateMessageService extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final String SERVICE_NAME = "Get statistics from Artik Cloud";

  @Override
  public void doGet(
      HttpServletRequest httpRequest, 
      HttpServletResponse httpResponse) 
          throws IOException {

    Util.setCorsHeadersAndContentType(httpResponse);
    TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
    
    try {
      GetMessageStatsResponse svcResponse = processRequest(httpRequest);
      String json =  svcResponse.toJsonString();
      httpResponse.getWriter().print(json);
    } catch (Exception e) {
      httpResponse.getWriter().println(e);
    }
    
  }
  
  private static GetMessageStatsResponse processRequest(HttpServletRequest httpRequest) {
    String servlet = httpRequest.getServletPath();
    
    String queryString = httpRequest.getRequestURL()
        .append("?")
        .append(httpRequest.getQueryString())
        .toString();
    
    Map<String, String[]> params = httpRequest.getParameterMap();
    
    MessageStatsRequestData requestData = new MessageStatsRequestData(params);
    
    AggregateMessageReturn messageReturn = ArtikCloud.getMessageStats(
        requestData.device, 
        requestData.days, 
        requestData.hours, 
        requestData.minutes);
 
    GetMessageStatsResponse response = GetMessageStatsResponse.builder()
        .name(SERVICE_NAME)
        .status("Success")
        .url(queryString)
        .requestData(requestData)
        .response(messageReturn.getData())
        .build();
    
    return response;
  }
  
}
