package com.clayton.smarthome;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clayton.smarthome.ArtikCloud.MessageData;
import com.clayton.smarthome.ArtikCloud.MessageReturn;
import com.google.gson.JsonElement;

/**
 * Servlet implementation class ToggleACService
 */
@WebServlet(
    name = "Toggle AC state",
    description = "Turn on and off the AC",
    urlPatterns = {
        "/turnOnAC",
        "/turnOffAC"
    })
public class ToggleACService extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	@Override
	protected void doGet(
	    HttpServletRequest request, 
	    HttpServletResponse response) 
	        throws ServletException, IOException {
	  
	  Util.setCorsHeadersAndContentType(response);
	  TimeZone.setDefault(TimeZone.getTimeZone("America/Denver"));
	  
	  try {
      String servlet = request.getServletPath();
      
      if ("/turnOnAC".equals(servlet)) {
        toggleAC(State.on);
        response.getWriter().print("Turning AC on");
      } else if ("/turnOffAC".equals(servlet)) {
        toggleAC(State.off);
        response.getWriter().print("Turning AC off");
      }
	  } catch (Exception e) {
	    response.getWriter().print(e);
	  }
	}
	
	private void toggleAC(String state) throws IOException {
	  Map<String, Object> messageData = new HashMap<>();
	  messageData.put(DeviceField.STATE.id, state);
	  ArtikCloud.postMessage(Device.AC, messageData);
	  
	  MessageReturn results = ArtikCloud.getLastMessage(DeviceGroup.TEMPERATURE, 1);
	  MessageData temperature = results.getDeviceData(Device.TEMPERATURE).get(0);
	  String webhooksUrl = new WebhooksUrl(state).url + "?"; 
	 
	  Map<String, JsonElement> temperatureData = new TreeMap<>(temperature.data);
	  int index = 0;

	  for (String key : temperatureData.keySet()) {
	    index++;
	    String keyFmt = key.replace("_", "+");
	    webhooksUrl += "value" + index + "=" + keyFmt + ":+" + 
	          temperatureData.get(key).toString() + "&";
	  }
	  
	  URL url = new URL(webhooksUrl);
	  url.openStream();
	}
	
	private static class State {
	  static String on = "on";
	  static String off = "off";
	}
	
	private static class WebhooksUrl {
	  String url;
	  private String on = "https://maker.ifttt.com/trigger/turnOnAC/with/key/" + 
	        "iufmWbuaRVoq6797i1PYSTZhAIL71AkURIn_6J5P-2w";
	  private String off = "https://maker.ifttt.com/trigger/turnOffAC/with/key/" +
	        "iufmWbuaRVoq6797i1PYSTZhAIL71AkURIn_6J5P-2w";
	  
	  WebhooksUrl(String state) {
	    url = State.on.equals(state) ? on : off;
	  }
	}

}
